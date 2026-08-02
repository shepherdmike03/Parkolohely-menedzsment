#!/usr/bin/env python3


#  ____  _                _                  _ __  __ _ _                     _    ___ 
# / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____     _       / \  |_ _|
# \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \  _| |_    / _ \  | | 
#  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/ |_   _|  / ___ \ | | 
# |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|   |_|   /_/   \_\___|
#                  |_|                                                                 

from __future__ import annotations

import argparse
import json
import os
import sys
import urllib.error
import urllib.parse
import urllib.request
from datetime import datetime, timedelta, timezone
from typing import Any, Callable


RESET = "\033[0m"
BOLD = "\033[1m"
RED = "\033[31m"
GREEN = "\033[32m"
YELLOW = "\033[33m"
CYAN = "\033[36m"
DIM = "\033[2m"


class Failure(Exception):
    pass


class Client:
    def __init__(self, base_url: str) -> None:
        self.base_url = base_url.rstrip("/")

    def request(
        self,
        method: str,
        path: str,
        *,
        query: dict[str, Any] | None = None,
        body: dict[str, Any] | None = None,
    ) -> tuple[int, Any]:
        url = f"{self.base_url}{path}"

        if query:
            url += "?" + urllib.parse.urlencode(query)

        data = None
        headers = {"Accept": "application/json"}

        if body is not None:
            data = json.dumps(body).encode("utf-8")
            headers["Content-Type"] = "application/json"

        request = urllib.request.Request(
            url,
            data=data,
            headers=headers,
            method=method,
        )

        try:
            with urllib.request.urlopen(request, timeout=20) as response:
                raw = response.read().decode("utf-8")
                return response.status, json.loads(raw) if raw else None

        except urllib.error.HTTPError as error:
            raw = error.read().decode("utf-8")

            try:
                payload = json.loads(raw) if raw else None
            except json.JSONDecodeError:
                payload = raw

            return error.code, payload

        except urllib.error.URLError as error:
            raise Failure(
                f"Nem lehet kapcsolodni az API-hoz: {error.reason}"
            ) from error


class Runner:
    def __init__(self, client: Client, verbose: bool) -> None:
        self.client = client
        self.verbose = verbose
        self.ok = 0
        self.failed = 0
        self.skipped = 0

    def call(
        self,
        method: str,
        path: str,
        expected_status: int,
        *,
        query: dict[str, Any] | None = None,
        body: dict[str, Any] | None = None,
    ) -> Any:
        status, payload = self.client.request(
            method,
            path,
            query=query,
            body=body,
        )

        if self.verbose:
            print(
                f"{DIM}{method} {path} -> HTTP {status}\n"
                f"{json.dumps(payload, ensure_ascii=False, indent=2)}"
                f"{RESET}"
            )

        if status != expected_status:
            raise Failure(
                f"Vart HTTP {expected_status}, kapott HTTP {status}\n"
                f"{json.dumps(payload, ensure_ascii=False, indent=2)}"
            )

        return payload

    def test(
        self,
        name: str,
        function: Callable[[], None],
        *,
        critical: bool = False,
    ) -> bool:
        print(f"{CYAN}TEST{RESET}  {name}")

        try:
            function()

        except (
            Failure,
            AssertionError,
            KeyError,
            TypeError,
            ValueError,
        ) as error:
            self.failed += 1
            print(f"{RED}{BOLD}FAIL{RESET}  {error}\n")
            return not critical

        self.ok += 1
        print(f"{GREEN}{BOLD}PASS{RESET}\n")
        return True

    def skip(self, name: str, reason: str) -> None:
        self.skipped += 1
        print(
            f"{YELLOW}{BOLD}SKIP{RESET}  {name}\n"
            f"      {reason}\n"
        )

    @staticmethod
    def require(condition: bool, message: str) -> None:
        if not condition:
            raise Failure(message)

    @staticmethod
    def as_dict(payload: Any) -> dict[str, Any]:
        if not isinstance(payload, dict):
            raise Failure(f"Nem JSON objektum erkezett: {payload!r}")
        return payload

    @staticmethod
    def as_list(payload: Any) -> list[Any]:
        if not isinstance(payload, list):
            raise Failure(f"Nem JSON lista erkezett: {payload!r}")
        return payload

    def summary(self) -> int:
        total = self.ok + self.failed + self.skipped

        print(f"\n{BOLD}{'=' * 54}{RESET}")
        print(f"{BOLD}TESZT OSSZESITES{RESET}")
        print(f"  Osszes teszt: {total}")
        print(f"  {GREEN}Sikeres:     {self.ok}{RESET}")
        print(f"  {RED}Sikertelen:  {self.failed}{RESET}")
        print(f"  {YELLOW}Kihagyva:    {self.skipped}{RESET}")
        print(f"{BOLD}{'=' * 54}{RESET}")

        return 0 if self.failed == 0 else 1


def utc_iso(value: datetime) -> str:
    return value.astimezone(timezone.utc).isoformat(
        timespec="seconds"
    ).replace("+00:00", "Z")


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Parkolohely-foglalas API integracios teszt."
    )
    parser.add_argument(
        "--base-url",
        default=os.getenv(
            "PARKOLO_API_URL",
            "http://localhost:3333",
        ),
    )
    parser.add_argument(
        "--skip-generator",
        action="store_true",
        help="Kihagyja a /dev/generate_data tesztet.",
    )
    parser.add_argument(
        "--verbose",
        action="store_true",
        help="Kiirja a teljes JSON valaszokat.",
    )
    args = parser.parse_args()

    runner = Runner(
        Client(args.base_url),
        args.verbose,
    )

    stamp = datetime.now(timezone.utc).strftime("%y%m%d%H%M%S")
    token = f"API{stamp}"

    normal_last_name = f"Normal{token}"
    blocked_last_name = f"Tiltott{token}"

    normal_plate = f"TST-{stamp}-N"
    blocked_plate = f"TST-{stamp}-B"

    start_dt = (
        datetime.now(timezone.utc)
        + timedelta(days=2)
    ).replace(second=0, microsecond=0)

    start = utc_iso(start_dt)
    end = utc_iso(start_dt + timedelta(hours=2))
    invalid_end = utc_iso(start_dt - timedelta(hours=1))
    blacklist_end = utc_iso(
        datetime.now(timezone.utc)
        + timedelta(days=30)
    )

    state: dict[str, Any] = {
        "category_id": None,
        "spot_ids": [],
        "normal_person_id": None,
        "normal_vehicle_id": None,
        "blocked_person_id": None,
        "blocked_vehicle_id": None,
        "blacklist_id": None,
        "reservation_id": None,
        "reservation_deleted": False,
    }

    print(
        f"{BOLD}{CYAN}"
        "PARKOLOHELY-FOGLALAS API TESZT"
        f"{RESET}"
    )
    print(f"API: {args.base_url}")
    print(f"Tesztazonosito: {token}\n")

    def health() -> None:
        payload = runner.as_dict(
            runner.call("GET", "/health", 200)
        )
        runner.require(
            payload.get("status") == "healthy",
            "A status nem healthy.",
        )

    if not runner.test(
        "GET /health",
        health,
        critical=True,
    ):
        return runner.summary()

    def categories() -> None:
        payload = runner.as_list(
            runner.call("GET", "/categories", 200)
        )
        runner.require(
            len(payload) > 0,
            "Nincs kategoria az adatbazisban.",
        )
        category = payload[0]
        runner.require(
            all(
                key in category
                for key in (
                    "kategoriaId",
                    "kategoriaNev",
                    "meretSorrend",
                )
            ),
            "A kategoria valasz hianyos.",
        )
        state["category_id"] = int(category["kategoriaId"])

    if not runner.test(
        "GET /categories",
        categories,
        critical=True,
    ):
        return runner.summary()

    def create_spots() -> None:
        payload = runner.as_dict(
            runner.call(
                "POST",
                "/new_spot",
                201,
                body={
                    "darab": 3,
                    "kategoriaId": state["category_id"],
                },
            )
        )
        runner.require(
            payload.get("letrehozottDarab") == 3,
            "Nem 3 hely jott letre.",
        )
        spots = runner.as_list(
            payload.get("letrehozottHelyek")
        )
        runner.require(
            len(spots) == 3,
            "A helylista merete nem 3.",
        )
        state["spot_ids"] = [
            int(spot["parkolohelyId"])
            for spot in spots
        ]

    if not runner.test(
        "POST /new_spot",
        create_spots,
        critical=True,
    ):
        return runner.summary()

    def invalid_spot() -> None:
        payload = runner.as_dict(
            runner.call(
                "POST",
                "/new_spot",
                400,
                body={
                    "darab": 0,
                    "kategoriaId": state["category_id"],
                },
            )
        )
        runner.require(
            "error" in payload,
            "Hianyzik az error mezo.",
        )

    runner.test(
        "POST /new_spot - hibas darabszam",
        invalid_spot,
    )

    def list_spots() -> None:
        payload = runner.as_dict(
            runner.call("GET", "/list_spots", 200)
        )
        spots = runner.as_list(payload.get("helyek"))
        ids = {
            int(spot["parkolohelyId"])
            for spot in spots
        }
        runner.require(
            set(state["spot_ids"]).issubset(ids),
            "A letrehozott helyek nincsenek a listaban.",
        )
        runner.require(
            int(payload["osszesen"]) == len(spots),
            "Az osszesen nem egyezik a lista meretevel.",
        )

    runner.test(
        "GET /list_spots",
        list_spots,
    )

    def register_customer(
        last_name: str,
        plate: str,
    ) -> dict[str, Any]:
        return runner.as_dict(
            runner.call(
                "POST",
                "/register_customer_vehicle",
                201,
                body={
                    "keresztnev": "Teszt",
                    "csaladnev": last_name,
                    "specialisStatusId": None,
                    "kategoriaId": state["category_id"],
                    "rendszam": plate,
                },
            )
        )

    def register_normal() -> None:
        payload = register_customer(
            normal_last_name,
            normal_plate,
        )
        state["normal_person_id"] = int(
            payload["tulajdonosId"]
        )
        state["normal_vehicle_id"] = int(
            payload["jarmuId"]
        )
        runner.require(
            payload["rendszam"] == normal_plate,
            "Rossz rendszam erkezett vissza.",
        )

    if not runner.test(
        "POST /register_customer_vehicle - normal ugyfel",
        register_normal,
        critical=True,
    ):
        return runner.summary()

    def register_blocked() -> None:
        payload = register_customer(
            blocked_last_name,
            blocked_plate,
        )
        state["blocked_person_id"] = int(
            payload["tulajdonosId"]
        )
        state["blocked_vehicle_id"] = int(
            payload["jarmuId"]
        )

    if not runner.test(
        "POST /register_customer_vehicle - tiltando ugyfel",
        register_blocked,
        critical=True,
    ):
        return runner.summary()

    def duplicate_plate() -> None:
        payload = runner.as_dict(
            runner.call(
                "POST",
                "/register_customer_vehicle",
                409,
                body={
                    "keresztnev": "Masik",
                    "csaladnev": normal_last_name,
                    "specialisStatusId": None,
                    "kategoriaId": state["category_id"],
                    "rendszam": normal_plate,
                },
            )
        )
        runner.require(
            "error" in payload,
            "Hianyzik az error mezo.",
        )

    runner.test(
        "POST /register_customer_vehicle - duplikalt rendszam",
        duplicate_plate,
    )

    def search_persons() -> None:
        payload = runner.as_list(
            runner.call(
                "GET",
                "/search/persons",
                200,
                query={"q": normal_last_name},
            )
        )
        runner.require(
            any(
                int(item["tulajdonosId"])
                == state["normal_person_id"]
                for item in payload
            ),
            "A szemely nem talalhato.",
        )

    runner.test(
        "GET /search/persons",
        search_persons,
    )

    def person_by_id() -> None:
        payload = runner.as_dict(
            runner.call(
                "GET",
                f"/search/persons/{state['normal_person_id']}",
                200,
            )
        )
        runner.require(
            int(payload["tulajdonosId"])
            == state["normal_person_id"],
            "Rossz szemely erkezett vissza.",
        )

    runner.test(
        "GET /search/persons/{id}",
        person_by_id,
    )

    def search_vehicles() -> None:
        payload = runner.as_list(
            runner.call(
                "GET",
                "/search/vehicles",
                200,
                query={"q": normal_plate},
            )
        )
        runner.require(
            any(
                int(item["jarmuId"])
                == state["normal_vehicle_id"]
                for item in payload
            ),
            "A jarmu nem talalhato.",
        )

    runner.test(
        "GET /search/vehicles",
        search_vehicles,
    )

    def vehicle_by_id() -> None:
        payload = runner.as_dict(
            runner.call(
                "GET",
                f"/search/vehicles/{state['normal_vehicle_id']}",
                200,
            )
        )
        runner.require(
            int(payload["jarmuId"])
            == state["normal_vehicle_id"],
            "Rossz jarmu erkezett vissza.",
        )

    runner.test(
        "GET /search/vehicles/{id}",
        vehicle_by_id,
    )

    def search_spots() -> None:
        spot_id = state["spot_ids"][0]
        spot = runner.as_dict(
            runner.call(
                "GET",
                f"/search/spots/{spot_id}",
                200,
            )
        )
        results = runner.as_list(
            runner.call(
                "GET",
                "/search/spots",
                200,
                query={
                    "q": spot["helyAzonosito"],
                },
            )
        )
        runner.require(
            any(
                int(item["parkolohelyId"]) == spot_id
                for item in results
            ),
            "A parkolohely nem talalhato.",
        )

    runner.test(
        "GET /search/spots es /search/spots/{id}",
        search_spots,
    )

    if args.skip_generator:
        runner.skip(
            "POST /dev/generate_data",
            "--skip-generator meg lett adva.",
        )
    else:
        def generator() -> None:
            payload = runner.as_dict(
                runner.call(
                    "POST",
                    "/dev/generate_data",
                    201,
                    body={
                        "persons": 1,
                        "vehiclesPerPerson": 1,
                        "spotsPerCategory": 0,
                        "pastReservations": 0,
                        "blacklistPercent": 0,
                    },
                )
            )
            runner.require(
                payload["personsCreated"] == 1,
                "Nem 1 szemely jott letre.",
            )
            runner.require(
                payload["vehiclesCreated"] == 1,
                "Nem 1 jarmu jott letre.",
            )

        runner.test(
            "POST /dev/generate_data",
            generator,
        )

    def free_spots() -> None:
        payload = runner.as_dict(
            runner.call(
                "GET",
                "/free_spots",
                200,
                query={
                    "from": start,
                    "to": end,
                },
            )
        )
        runner.require(
            int(payload["szabadHelyekSzama"]) > 0,
            "Nincs szabad hely.",
        )

    runner.test(
        "GET /free_spots",
        free_spots,
    )

    def compatible_free_spots() -> None:
        payload = runner.as_dict(
            runner.call(
                "GET",
                "/free_spots",
                200,
                query={
                    "from": start,
                    "to": end,
                    "jarmuId": state["normal_vehicle_id"],
                },
            )
        )
        runner.require(
            payload["jarmuId"]
            == state["normal_vehicle_id"],
            "Rossz jarmuId szerepel a valaszban.",
        )
        runner.require(
            int(payload["szabadHelyekSzama"]) > 0,
            "Nincs kompatibilis szabad hely.",
        )

    runner.test(
        "GET /free_spots jarmuId parameterrel",
        compatible_free_spots,
    )

    def spot_status() -> None:
        payload = runner.as_dict(
            runner.call(
                "GET",
                "/spot_status",
                200,
                query={
                    "from": start,
                    "to": end,
                },
            )
        )
        total = int(payload["osszesAktivHely"])
        occupied = int(payload["foglaltHely"])
        free = int(payload["szabadHely"])

        runner.require(
            total == occupied + free,
            "Az osszes hely nem foglalt + szabad.",
        )

    runner.test(
        "GET /spot_status",
        spot_status,
    )

    def invalid_interval() -> None:
        payload = runner.as_dict(
            runner.call(
                "GET",
                "/free_spots",
                400,
                query={
                    "from": start,
                    "to": invalid_end,
                },
            )
        )
        runner.require(
            "error" in payload,
            "Hianyzik az error mezo.",
        )

    runner.test(
        "GET /free_spots - hibas intervallum",
        invalid_interval,
    )

    def create_reservation() -> None:
        payload = runner.as_dict(
            runner.call(
                "POST",
                "/new_reservation",
                201,
                body={
                    "jarmuId": state["normal_vehicle_id"],
                    "kezdetIdo": start,
                    "vegIdo": end,
                },
            )
        )
        runner.require(
            payload["elfogadva"] is True,
            "A foglalas nincs elfogadva.",
        )
        reservation = runner.as_dict(payload["foglalas"])
        state["reservation_id"] = int(
            reservation["foglalasId"]
        )

    if not runner.test(
        "POST /new_reservation",
        create_reservation,
        critical=True,
    ):
        return runner.summary()

    try:
        def reservation_by_id() -> None:
            payload = runner.as_dict(
                runner.call(
                    "GET",
                    f"/reservation/{state['reservation_id']}",
                    200,
                )
            )
            runner.require(
                int(payload["foglalasId"])
                == state["reservation_id"],
                "Rossz foglalas erkezett vissza.",
            )

        runner.test(
            "GET /reservation/{id}",
            reservation_by_id,
        )

        def list_reservations() -> None:
            payload = runner.as_dict(
                runner.call(
                    "GET",
                    "/list_reservations",
                    200,
                )
            )
            reservations = runner.as_list(
                payload["foglalasok"]
            )
            runner.require(
                any(
                    int(item["foglalasId"])
                    == state["reservation_id"]
                    for item in reservations
                ),
                "A foglalas nincs a listaban.",
            )
            runner.require(
                int(payload["osszesen"])
                == len(reservations),
                "Az osszesen hibas.",
            )

        runner.test(
            "GET /list_reservations",
            list_reservations,
        )

        def overlapping_reservation() -> None:
            payload = runner.as_dict(
                runner.call(
                    "POST",
                    "/new_reservation",
                    409,
                    body={
                        "jarmuId": state["normal_vehicle_id"],
                        "kezdetIdo": start,
                        "vegIdo": end,
                    },
                )
            )
            runner.require(
                payload["elfogadva"] is False,
                "Az atfedo foglalas elfogadott.",
            )
            runner.require(
                payload["kod"] == "JARMU_MAR_FOGLALT",
                "Rossz hibakod erkezett.",
            )

        runner.test(
            "POST /new_reservation - atfedo foglalas",
            overlapping_reservation,
        )

        def blacklist_person() -> None:
            payload = runner.as_dict(
                runner.call(
                    "POST",
                    "/blacklist_person",
                    201,
                    body={
                        "tulajdonosId":
                            state["blocked_person_id"],
                        "ok":
                            f"Automatikus API teszt {token}",
                        "tiltasVege":
                            blacklist_end,
                    },
                )
            )
            state["blacklist_id"] = int(
                payload["tiltasId"]
            )
            runner.require(
                payload["aktiv"] is True,
                "A tiltas nem aktiv.",
            )

        if not runner.test(
            "POST /blacklist_person",
            blacklist_person,
            critical=True,
        ):
            return runner.summary()

        def search_blacklist() -> None:
            payload = runner.as_list(
                runner.call(
                    "GET",
                    "/search/blacklist",
                    200,
                    query={
                        "q": blocked_last_name,
                    },
                )
            )
            runner.require(
                any(
                    int(item["tiltasId"])
                    == state["blacklist_id"]
                    for item in payload
                ),
                "A tiltas nem talalhato.",
            )

        runner.test(
            "GET /search/blacklist",
            search_blacklist,
        )

        def blacklist_by_id() -> None:
            payload = runner.as_dict(
                runner.call(
                    "GET",
                    f"/search/blacklist/{state['blacklist_id']}",
                    200,
                )
            )
            runner.require(
                int(payload["tiltasId"])
                == state["blacklist_id"],
                "Rossz tiltas erkezett vissza.",
            )

        runner.test(
            "GET /search/blacklist/{id}",
            blacklist_by_id,
        )

        def blocked_reservation() -> None:
            payload = runner.as_dict(
                runner.call(
                    "POST",
                    "/new_reservation",
                    403,
                    body={
                        "jarmuId":
                            state["blocked_vehicle_id"],
                        "kezdetIdo":
                            start,
                        "vegIdo":
                            end,
                    },
                )
            )
            runner.require(
                payload["elfogadva"] is False,
                "A tiltott szemely foglalasa elfogadott.",
            )
            runner.require(
                payload["kod"] == "TULAJDONOS_TILTOTT",
                "Rossz hibakod erkezett.",
            )

        runner.test(
            "POST /new_reservation - tiltott tulajdonos",
            blocked_reservation,
        )

        def cancel_reservation() -> None:
            runner.call(
                "DELETE",
                f"/reservation/{state['reservation_id']}",
                204,
            )
            state["reservation_deleted"] = True

        runner.test(
            "DELETE /reservation/{id}",
            cancel_reservation,
        )

        def deleted_reservation() -> None:
            payload = runner.as_dict(
                runner.call(
                    "GET",
                    f"/reservation/{state['reservation_id']}",
                    404,
                )
            )
            runner.require(
                "error" in payload,
                "Hianyzik az error mezo.",
            )

        runner.test(
            "GET /reservation/{id} - torles utan",
            deleted_reservation,
        )

    finally:
        if (
            state["reservation_id"] is not None
            and not state["reservation_deleted"]
        ):
            print(
                f"{YELLOW}"
                "Cleanup: tesztfoglalas torlese..."
                f"{RESET}"
            )
            status, _ = runner.client.request(
                "DELETE",
                f"/reservation/{state['reservation_id']}",
            )
            if status == 204:
                print(
                    f"{GREEN}"
                    "Cleanup sikeres."
                    f"{RESET}"
                )
            else:
                print(
                    f"{RED}"
                    f"Cleanup sikertelen, HTTP {status}."
                    f"{RESET}"
                )

    return runner.summary()


if __name__ == "__main__":
    sys.exit(main())

