#!/usr/bin/env python3


#  ____  _                _                  _ __  __ _ _                     _    ___ 
# / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____     _       / \  |_ _|
# \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \  _| |_    / _ \  | | 
#  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/ |_   _|  / ___ \ | | 
# |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|   |_|   /_/   \_\___|
#                  |_|                                                                 


from __future__ import annotations

import json
import os
import urllib.error
import urllib.parse
import urllib.request
from datetime import datetime
from typing import Any


BASE_URL = os.getenv(
    "PARKOLO_API_URL",
    "http://localhost:3333",
).rstrip("/")


RESET = "\033[0m"
BOLD = "\033[1m"
DIM = "\033[2m"
RED = "\033[31m"
GREEN = "\033[32m"
YELLOW = "\033[33m"
BLUE = "\033[34m"
MAGENTA = "\033[35m"
CYAN = "\033[36m"


class ApiClient:
    def request(
        self,
        method: str,
        path: str,
        *,
        query: dict[str, Any] | None = None,
        body: dict[str, Any] | None = None,
    ) -> tuple[int, Any]:
        url = f"{BASE_URL}{path}"

        if query:
            url += "?" + urllib.parse.urlencode(query)

        data: bytes | None = None
        headers = {
            "Accept": "application/json",
        }

        if body is not None:
            data = json.dumps(body).encode("utf-8")
            headers["Content-Type"] = "application/json"

        request = urllib.request.Request(
            url=url,
            data=data,
            headers=headers,
            method=method,
        )

        try:
            with urllib.request.urlopen(
                request,
                timeout=20,
            ) as response:
                raw = response.read().decode("utf-8")

                return (
                    response.status,
                    json.loads(raw) if raw else None,
                )

        except urllib.error.HTTPError as error:
            raw = error.read().decode("utf-8")

            try:
                payload = json.loads(raw) if raw else None
            except json.JSONDecodeError:
                payload = raw

            return error.code, payload

        except (
            urllib.error.URLError,
            TimeoutError,
        ) as error:
            reason = getattr(
                error,
                "reason",
                error,
            )

            return 0, {
                "error": (
                    "Nem lehet kapcsolódni az API-hoz: "
                    f"{reason}"
                )
            }


api = ApiClient()


def clear() -> None:
    os.system(
        "cls"
        if os.name == "nt"
        else "clear"
    )


def header(
    title: str = "Főmenü",
) -> None:
    clear()

    print(
        f"{BOLD}{CYAN}"
        "╔══════════════════════════════════════════════════════╗\n"
        "║              PARKOLÓHELY-FOGLALÁS CLI               ║\n"
        "╚══════════════════════════════════════════════════════╝"
        f"{RESET}"
    )

    print(
        f"{DIM}API: {BASE_URL}{RESET}"
    )

    print(
        f"{BLUE}{'─' * 56}{RESET}"
    )

    print(
        f"{BOLD}{MAGENTA}{title}{RESET}\n"
    )


def success(
    text: str,
) -> None:
    print(
        f"{GREEN}{BOLD}✓ {text}{RESET}"
    )


def warning(
    text: str,
) -> None:
    print(
        f"{YELLOW}{BOLD}! {text}{RESET}"
    )


def failure(
    text: str,
) -> None:
    print(
        f"{RED}{BOLD}✗ {text}{RESET}"
    )


def pause() -> None:
    input(
        f"\n{DIM}"
        "Nyomj Entert a folytatáshoz..."
        f"{RESET}"
    )


def error_message(
    payload: Any,
) -> str:
    if isinstance(payload, dict):
        return str(
            payload.get("error")
            or payload.get("uzenet")
            or payload.get("message")
            or payload.get("kod")
            or payload
        )

    return str(
        payload
        or "Az API nem adott hibaüzenetet."
    )


def ask_int(
    prompt: str,
    minimum: int = 0,
    maximum: int = 2**63 - 1,
    default: int | None = None,
) -> int:
    while True:
        suffix = (
            f" [{default}]"
            if default is not None
            else ""
        )

        raw_value = input(
            f"{CYAN}{prompt}{suffix}: {RESET}"
        ).strip()

        if (
            not raw_value
            and default is not None
        ):
            return default

        try:
            number = int(raw_value)

        except ValueError:
            failure(
                "Egész számot írj be."
            )
            continue

        if minimum <= number <= maximum:
            return number

        failure(
            f"A számnak {minimum} és {maximum} "
            "között kell lennie."
        )


def ask_non_empty(
    prompt: str,
    *,
    allow_back: bool = False,
    maximum_length: int | None = None,
) -> str | None:
    while True:
        value = input(
            f"{CYAN}{prompt}: {RESET}"
        ).strip()

        if (
            allow_back
            and value == "0"
        ):
            return None

        if not value:
            failure(
                "Ez a mező nem maradhat üresen."
            )
            continue

        if (
            maximum_length is not None
            and len(value) > maximum_length
        ):
            failure(
                f"Legfeljebb {maximum_length} "
                "karakter adható meg."
            )
            continue

        return value


def confirm(
    prompt: str,
) -> bool:
    answer = input(
        f"{YELLOW}{prompt} [i/N]: {RESET}"
    ).strip().lower()

    return answer in {
        "i",
        "igen",
        "y",
        "yes",
    }


def ask_future_interval(
) -> tuple[str, str] | None:
    while True:
        raw_start = input(
            f"{CYAN}"
            "Kezdés (YYYY-MM-DD HH:MM, "
            "0 = vissza): "
            f"{RESET}"
        ).strip()

        if raw_start == "0":
            return None

        raw_end = input(
            f"{CYAN}"
            "Befejezés (YYYY-MM-DD HH:MM, "
            "0 = vissza): "
            f"{RESET}"
        ).strip()

        if raw_end == "0":
            return None

        try:
            start = datetime.strptime(
                raw_start,
                "%Y-%m-%d %H:%M",
            ).astimezone()

            end = datetime.strptime(
                raw_end,
                "%Y-%m-%d %H:%M",
            ).astimezone()

        except ValueError:
            failure(
                "Érvénytelen dátumformátum."
            )
            continue

        now = datetime.now().astimezone()

        if start <= now:
            failure(
                "A kezdésnek a jövőben "
                "kell lennie."
            )
            continue

        if end <= start:
            failure(
                "A befejezésnek a kezdés "
                "után kell lennie."
            )
            continue

        return (
            start.isoformat(
                timespec="seconds",
            ),
            end.isoformat(
                timespec="seconds",
            ),
        )


def table(
    headers: list[str],
    rows: list[list[Any]],
) -> None:
    normalized = [
        [
            "-"
            if value is None
            else str(value)
            for value in row
        ]
        for row in rows
    ]

    widths = [
        len(header_name)
        for header_name in headers
    ]

    for row in normalized:
        for index, value in enumerate(row):
            widths[index] = max(
                widths[index],
                min(
                    len(value),
                    45,
                ),
            )

    def render(
        row: list[str],
    ) -> str:
        cells: list[str] = []

        for index, value in enumerate(row):
            clipped = (
                value
                if len(value) <= 45
                else value[:42] + "..."
            )

            cells.append(
                clipped.ljust(
                    widths[index]
                )
            )

        return "  ".join(cells)

    print(
        f"{BOLD}{render(headers)}{RESET}"
    )

    print(
        f"{DIM}"
        + render(
            [
                "─" * width
                for width in widths
            ]
        )
        + f"{RESET}"
    )

    if not normalized:
        print(
            f"{DIM}(nincs találat){RESET}"
        )
        return

    for row in normalized:
        print(
            render(row)
        )


def boolean_text(
    value: Any,
) -> str:
    return (
        "igen"
        if value
        else "nem"
    )


def ensure_api() -> None:
    status, payload = api.request(
        "GET",
        "/health",
    )

    if (
        status != 200
        or not isinstance(payload, dict)
        or payload.get("status") != "healthy"
    ):
        if status == 0:
            failure(
                error_message(payload)
            )
        else:
            failure(
                "Az API health check sikertelen: "
                f"HTTP {status}"
            )

        raise SystemExit(1)


def choose_person(
) -> dict[str, Any] | None:
    while True:
        query = input(
            f"{CYAN}"
            "Tulajdonos neve vagy ID-je "
            "(Enter = minden, 0 = vissza): "
            f"{RESET}"
        ).strip()

        if query == "0":
            return None

        status, persons = api.request(
            "GET",
            "/search/persons",
            query={
                "q": query,
            },
        )

        if status != 200:
            failure(
                error_message(persons)
            )
            continue

        if not persons:
            warning(
                "Nincs találat. Próbáld újra."
            )
            continue

        table(
            [
                "ID",
                "NÉV",
                "SPECIÁLIS STÁTUSZ",
            ],
            [
                [
                    person["tulajdonosId"],
                    person["nev"],
                    person.get(
                        "specialisStatusId"
                    ),
                ]
                for person in persons
            ],
        )

        selected_id = ask_int(
            "Tulajdonos ID (0 = vissza)",
            0,
        )

        if selected_id == 0:
            return None

        person = next(
            (
                item
                for item in persons
                if item["tulajdonosId"]
                == selected_id
            ),
            None,
        )

        if person is not None:
            return person

        failure(
            "Ez az ID nincs a megjelenített "
            "találatok között."
        )


def choose_vehicle(
    owner_id: int,
) -> dict[str, Any] | None:
    while True:
        query = input(
            f"{CYAN}"
            "Rendszám vagy járműkategória "
            "(Enter = minden, 0 = vissza): "
            f"{RESET}"
        ).strip()

        if query == "0":
            return None

        status, vehicles = api.request(
            "GET",
            "/search/vehicles",
            query={
                "q": query,
            },
        )

        if status != 200:
            failure(
                error_message(vehicles)
            )
            continue

        owner_vehicles = [
            vehicle
            for vehicle in vehicles
            if vehicle["tulajdonosId"]
            == owner_id
        ]

        if not owner_vehicles:
            warning(
                "Ehhez a tulajdonoshoz "
                "nincs ilyen jármű."
            )
            continue

        table(
            [
                "ID",
                "RENDSZÁM",
                "KATEGÓRIA",
            ],
            [
                [
                    vehicle["jarmuId"],
                    vehicle["rendszam"],
                    vehicle["kategoriaNev"],
                ]
                for vehicle in owner_vehicles
            ],
        )

        selected_id = ask_int(
            "Jármű ID (0 = vissza)",
            0,
        )

        if selected_id == 0:
            return None

        vehicle = next(
            (
                item
                for item in owner_vehicles
                if item["jarmuId"]
                == selected_id
            ),
            None,
        )

        if vehicle is not None:
            return vehicle

        failure(
            "Ez az ID nincs a megjelenített "
            "találatok között."
        )


def choose_category(
) -> dict[str, Any] | None:
    status, categories = api.request(
        "GET",
        "/categories",
    )

    if status != 200:
        failure(
            error_message(categories)
        )
        pause()
        return None

    if not categories:
        failure(
            "Nincsenek járműkategóriák "
            "az adatbázisban."
        )
        pause()
        return None

    while True:
        table(
            [
                "ID",
                "KATEGÓRIA",
                "MÉRETSORREND",
            ],
            [
                [
                    category["kategoriaId"],
                    category["kategoriaNev"],
                    category["meretSorrend"],
                ]
                for category in categories
            ],
        )

        selected_id = ask_int(
            "Kategória ID (0 = vissza)",
            0,
        )

        if selected_id == 0:
            return None

        category = next(
            (
                item
                for item in categories
                if item["kategoriaId"]
                == selected_id
            ),
            None,
        )

        if category is not None:
            return category

        failure(
            "Nincs ilyen kategória-ID."
        )


def register_new_customer_vehicle(
) -> tuple[
    dict[str, Any],
    dict[str, Any],
] | None:
    header(
        "Új személy és jármű felvétele"
    )

    print(
        f"{DIM}"
        "A név vagy rendszám megadásánál "
        "írj 0-t a visszalépéshez."
        f"{RESET}\n"
    )

    keresztnev = ask_non_empty(
        "Keresztnév (0 = vissza)",
        allow_back=True,
        maximum_length=100,
    )

    if keresztnev is None:
        return None

    csaladnev = ask_non_empty(
        "Családnév (0 = vissza)",
        allow_back=True,
        maximum_length=100,
    )

    if csaladnev is None:
        return None

    print()

    category = choose_category()

    if category is None:
        return None

    rendszam = ask_non_empty(
        "Rendszám (0 = vissza)",
        allow_back=True,
        maximum_length=20,
    )

    if rendszam is None:
        return None

    rendszam = rendszam.upper()

    header(
        "Új személy és jármű — összegzés"
    )

    table(
        [
            "MEZŐ",
            "ÉRTÉK",
        ],
        [
            [
                "Név",
                f"{csaladnev} {keresztnev}",
            ],
            [
                "Speciális státusz",
                "nincs",
            ],
            [
                "Rendszám",
                rendszam,
            ],
            [
                "Kategória",
                category["kategoriaNev"],
            ],
        ],
    )

    print(
        "\n  1. Létrehozás"
    )

    print(
        "  0. Vissza\n"
    )

    choice = ask_int(
        "Választás",
        0,
        1,
    )

    if choice == 0:
        return None

    status, payload = api.request(
        "POST",
        "/register_customer_vehicle",
        body={
            "keresztnev":
                keresztnev,

            "csaladnev":
                csaladnev,

            "specialisStatusId":
                None,

            "kategoriaId":
                category["kategoriaId"],

            "rendszam":
                rendszam,
        },
    )

    if status != 201:
        failure(
            f"HTTP {status}: "
            f"{error_message(payload)}"
        )
        pause()
        return None

    success(
        "A személy és a jármű létrejött."
    )

    person = {
        "tulajdonosId":
            payload["tulajdonosId"],

        "nev":
            payload["tulajdonosNev"],

        "specialisStatusId":
            None,
    }

    vehicle = {
        "jarmuId":
            payload["jarmuId"],

        "rendszam":
            payload["rendszam"],

        "tulajdonosId":
            payload["tulajdonosId"],

        "tulajdonosNev":
            payload["tulajdonosNev"],

        "kategoriaId":
            payload["kategoriaId"],

        "kategoriaNev":
            payload["kategoriaNev"],
    }

    return person, vehicle


def submit_reservation(
    person: dict[str, Any],
    vehicle: dict[str, Any],
) -> None:
    header(
        "Foglalási időszak megadása"
    )

    table(
        [
            "MEZŐ",
            "ÉRTÉK",
        ],
        [
            [
                "Tulajdonos",
                person["nev"],
            ],
            [
                "Rendszám",
                vehicle["rendszam"],
            ],
            [
                "Járműkategória",
                vehicle["kategoriaNev"],
            ],
        ],
    )

    print()

    interval = ask_future_interval()

    if interval is None:
        return

    start, end = interval

    status, free_spots = api.request(
        "GET",
        "/free_spots",
        query={
            "from":
                start,

            "to":
                end,

            "jarmuId":
                vehicle["jarmuId"],
        },
    )

    if status != 200:
        failure(
            error_message(free_spots)
        )
        pause()
        return

    if free_spots["szabadHelyekSzama"] == 0:
        failure(
            "Nincs kompatibilis szabad hely "
            "ebben az időszakban."
        )
        pause()
        return

    header(
        "Foglalás összegzése"
    )

    table(
        [
            "MEZŐ",
            "ÉRTÉK",
        ],
        [
            [
                "Tulajdonos",
                person["nev"],
            ],
            [
                "Tulajdonos ID",
                person["tulajdonosId"],
            ],
            [
                "Rendszám",
                vehicle["rendszam"],
            ],
            [
                "Járműkategória",
                vehicle["kategoriaNev"],
            ],
            [
                "Kezdés",
                start,
            ],
            [
                "Befejezés",
                end,
            ],
            [
                "Kompatibilis szabad helyek",
                free_spots[
                    "szabadHelyekSzama"
                ],
            ],
        ],
    )

    print(
        "\n  1. Foglalás létrehozása"
    )

    print(
        "  0. Vissza\n"
    )

    choice = ask_int(
        "Választás",
        0,
        1,
    )

    if choice == 0:
        return

    status, payload = api.request(
        "POST",
        "/new_reservation",
        body={
            "jarmuId":
                vehicle["jarmuId"],

            "kezdetIdo":
                start,

            "vegIdo":
                end,
        },
    )

    print()

    if status == 201:
        reservation = payload["foglalas"]

        success(
            "Foglalás létrehozva."
        )

        table(
            [
                "MEZŐ",
                "ÉRTÉK",
            ],
            [
                [
                    "Foglalás ID",
                    reservation["foglalasId"],
                ],
                [
                    "Parkolóhely",
                    reservation["helyAzonosito"],
                ],
                [
                    "Állapot",
                    reservation["allapot"],
                ],
            ],
        )

    else:
        failure(
            f"HTTP {status}: "
            f"{error_message(payload)}"
        )

    pause()


def reservation_menu() -> None:
    while True:
        header(
            "1. Foglalás kérése"
        )

        print(
            "  1. Új személy és új jármű"
        )

        print(
            "  2. Meglévő személy és jármű"
        )

        print(
            "  0. Vissza\n"
        )

        choice = ask_int(
            "Választás",
            0,
            2,
        )

        if choice == 0:
            return

        if choice == 1:
            registered = (
                register_new_customer_vehicle()
            )

            if registered is None:
                continue

            person, vehicle = registered

            submit_reservation(
                person,
                vehicle,
            )

            continue

        header(
            "Meglévő személy kiválasztása"
        )

        person = choose_person()

        if person is None:
            continue

        header(
            "Meglévő jármű kiválasztása"
        )

        print(
            f"{BOLD}"
            f"Tulajdonos: {person['nev']}"
            f"{RESET}\n"
        )

        vehicle = choose_vehicle(
            person["tulajdonosId"]
        )

        if vehicle is None:
            continue

        submit_reservation(
            person,
            vehicle,
        )


def generate_data() -> None:
    while True:
        header(
            "2. Tesztadat-generálás"
        )

        print(
            "  1. Generálás beállítása"
        )

        print(
            "  0. Vissza\n"
        )

        choice = ask_int(
            "Választás",
            0,
            1,
        )

        if choice == 0:
            return

        header(
            "Tesztadat-generálás beállítása"
        )

        print(
            f"{DIM}"
            "A generált foglalások "
            "a múltba kerülnek."
            f"{RESET}\n"
        )

        persons = ask_int(
            "Személyek száma "
            "(0 = vissza)",
            0,
            1000,
            30,
        )

        if persons == 0:
            continue

        request_body = {
            "persons":
                persons,

            "vehiclesPerPerson":
                ask_int(
                    "Járművek személyenként",
                    1,
                    5,
                    1,
                ),

            "spotsPerCategory":
                ask_int(
                    "Parkolóhelyek kategóriánként",
                    0,
                    200,
                    10,
                ),

            "pastReservations":
                ask_int(
                    "Múltbeli foglalások száma",
                    0,
                    5000,
                    60,
                ),

            "blacklistPercent":
                ask_int(
                    "Tiltólistára kerülők "
                    "aránya (%)",
                    0,
                    100,
                    20,
                ),
        }

        header(
            "Tesztadat-generálás összegzése"
        )

        table(
            [
                "BEÁLLÍTÁS",
                "ÉRTÉK",
            ],
            [
                [
                    "Személyek",
                    request_body["persons"],
                ],
                [
                    "Járművek személyenként",
                    request_body[
                        "vehiclesPerPerson"
                    ],
                ],
                [
                    "Helyek kategóriánként",
                    request_body[
                        "spotsPerCategory"
                    ],
                ],
                [
                    "Múltbeli foglalások",
                    request_body[
                        "pastReservations"
                    ],
                ],
                [
                    "Tiltólista aránya",
                    (
                        f"{request_body['blacklistPercent']}%"
                    ),
                ],
            ],
        )

        print(
            "\n  1. Generálás indítása"
        )

        print(
            "  0. Vissza\n"
        )

        choice = ask_int(
            "Választás",
            0,
            1,
        )

        if choice == 0:
            continue

        status, payload = api.request(
            "POST",
            "/dev/generate_data",
            body=request_body,
        )

        print()

        if status == 201:
            success(
                "A tesztadatok elkészültek."
            )

            table(
                [
                    "TÍPUS",
                    "DARAB",
                ],
                [
                    [
                        "Batch",
                        payload["batchId"],
                    ],
                    [
                        "Kategóriák",
                        payload["categoriesUsed"],
                    ],
                    [
                        "Személyek",
                        payload["personsCreated"],
                    ],
                    [
                        "Járművek",
                        payload["vehiclesCreated"],
                    ],
                    [
                        "Parkolóhelyek",
                        payload["spotsCreated"],
                    ],
                    [
                        "Tiltások",
                        payload["blacklistsCreated"],
                    ],
                    [
                        "Múltbeli foglalások",
                        payload[
                            "pastReservationsCreated"
                        ],
                    ],
                ],
            )

        else:
            failure(
                f"HTTP {status}: "
                f"{error_message(payload)}"
            )

        pause()


ENTITY_NAMES = {
    "persons":
        "Személyek",

    "vehicles":
        "Járművek",

    "spots":
        "Parkolóhelyek",

    "blacklist":
        "Tiltólista",

    "reservations":
        "Foglalások",
}


def entity_path(
    entity: str,
    item_id: int | None = None,
) -> str:
    paths = {
        "persons":
            "/search/persons",

        "vehicles":
            "/search/vehicles",

        "spots":
            "/search/spots",

        "blacklist":
            "/search/blacklist",

        "reservations":
            "/list_reservations",
    }

    if (
        entity == "reservations"
        and item_id is not None
    ):
        return (
            f"/reservation/{item_id}"
        )

    base_path = paths[entity]

    if item_id is None:
        return base_path

    return (
        f"{base_path}/{item_id}"
    )


def print_entity_list(
    entity: str,
    payload: Any,
) -> None:
    if entity == "persons":
        table(
            [
                "ID",
                "NÉV",
                "STÁTUSZ",
            ],
            [
                [
                    item["tulajdonosId"],
                    item["nev"],
                    item.get(
                        "specialisStatusId"
                    ),
                ]
                for item in payload
            ],
        )

        return

    if entity == "vehicles":
        table(
            [
                "ID",
                "RENDSZÁM",
                "TULAJDONOS",
                "KATEGÓRIA",
            ],
            [
                [
                    item["jarmuId"],
                    item["rendszam"],
                    item["tulajdonosNev"],
                    item["kategoriaNev"],
                ]
                for item in payload
            ],
        )

        return

    if entity == "spots":
        table(
            [
                "ID",
                "AZONOSÍTÓ",
                "AKTÍV",
                "KATEGÓRIA",
            ],
            [
                [
                    item["parkolohelyId"],
                    item["helyAzonosito"],
                    boolean_text(
                        item["aktiv"]
                    ),
                    item["kategoriaNev"],
                ]
                for item in payload
            ],
        )

        return

    if entity == "blacklist":
        table(
            [
                "ID",
                "TULAJDONOS",
                "OK",
                "KEZDET",
                "VÉGE",
                "AKTÍV",
            ],
            [
                [
                    item["tiltasId"],
                    item["tulajdonosNev"],
                    item["ok"],
                    item["tiltasKezdete"],
                    item.get(
                        "tiltasVege"
                    )
                    or "határozatlan",
                    boolean_text(
                        item["aktiv"]
                    ),
                ]
                for item in payload
            ],
        )

        return

    table(
        [
            "ID",
            "RENDSZÁM",
            "TULAJDONOS",
            "HELY",
            "KEZDET",
            "VÉGE",
            "ÁLLAPOT",
        ],
        [
            [
                item["foglalasId"],
                item["rendszam"],
                item["tulajdonosNev"],
                item["helyAzonosito"],
                item["kezdetIdo"],
                item["vegIdo"],
                item["allapot"],
            ]
            for item
            in payload["foglalasok"]
        ],
    )


def print_entity_detail(
    entity: str,
    item: dict[str, Any],
) -> None:
    if entity == "persons":
        rows = [
            [
                "Tulajdonos ID",
                item["tulajdonosId"],
            ],
            [
                "Név",
                item["nev"],
            ],
            [
                "Speciális státusz ID",
                item.get(
                    "specialisStatusId"
                ),
            ],
        ]

    elif entity == "vehicles":
        rows = [
            [
                "Jármű ID",
                item["jarmuId"],
            ],
            [
                "Rendszám",
                item["rendszam"],
            ],
            [
                "Tulajdonos ID",
                item["tulajdonosId"],
            ],
            [
                "Tulajdonos",
                item["tulajdonosNev"],
            ],
            [
                "Kategória ID",
                item["kategoriaId"],
            ],
            [
                "Kategória",
                item["kategoriaNev"],
            ],
        ]

    elif entity == "spots":
        rows = [
            [
                "Parkolóhely ID",
                item["parkolohelyId"],
            ],
            [
                "Azonosító",
                item["helyAzonosito"],
            ],
            [
                "Aktív",
                boolean_text(
                    item["aktiv"]
                ),
            ],
            [
                "Kategória ID",
                item["kategoriaId"],
            ],
            [
                "Kategória",
                item["kategoriaNev"],
            ],
        ]

    elif entity == "blacklist":
        rows = [
            [
                "Tiltás ID",
                item["tiltasId"],
            ],
            [
                "Tulajdonos ID",
                item["tulajdonosId"],
            ],
            [
                "Tulajdonos",
                item["tulajdonosNev"],
            ],
            [
                "Ok",
                item["ok"],
            ],
            [
                "Tiltás kezdete",
                item["tiltasKezdete"],
            ],
            [
                "Tiltás vége",
                item.get(
                    "tiltasVege"
                )
                or "határozatlan",
            ],
            [
                "Aktív",
                boolean_text(
                    item["aktiv"]
                ),
            ],
        ]

    else:
        rows = [
            [
                "Foglalás ID",
                item["foglalasId"],
            ],
            [
                "Jármű ID",
                item["jarmuId"],
            ],
            [
                "Rendszám",
                item["rendszam"],
            ],
            [
                "Tulajdonos ID",
                item["tulajdonosId"],
            ],
            [
                "Tulajdonos",
                item["tulajdonosNev"],
            ],
            [
                "Parkolóhely ID",
                item["parkolohelyId"],
            ],
            [
                "Parkolóhely",
                item["helyAzonosito"],
            ],
            [
                "Járműkategória",
                item["jarmuKategoria"],
            ],
            [
                "Helykategória",
                item["parkolohelyKategoria"],
            ],
            [
                "Kezdés",
                item["kezdetIdo"],
            ],
            [
                "Befejezés",
                item["vegIdo"],
            ],
            [
                "Érkezés",
                item.get(
                    "erkezesIdo"
                ),
            ],
            [
                "Elhagyás",
                item.get(
                    "elhagyasIdo"
                ),
            ],
            [
                "Létrehozva",
                item["letrehozva"],
            ],
            [
                "Állapot",
                item["allapot"],
            ],
        ]

    table(
        [
            "MEZŐ",
            "ÉRTÉK",
        ],
        rows,
    )


def show_entity_list(
    entity: str,
) -> None:
    query = ""

    if entity != "reservations":
        query = input(
            f"{CYAN}"
            "Szűrőszöveg "
            "(Enter = minden, 0 = vissza): "
            f"{RESET}"
        ).strip()

        if query == "0":
            return

    if entity == "reservations":
        status, payload = api.request(
            "GET",
            entity_path(entity),
        )

    else:
        status, payload = api.request(
            "GET",
            entity_path(entity),
            query={
                "q": query,
            },
        )

    header(
        f"Keresési eredmény — "
        f"{ENTITY_NAMES[entity]}"
    )

    if status != 200:
        failure(
            f"HTTP {status}: "
            f"{error_message(payload)}"
        )

    else:
        print_entity_list(
            entity,
            payload,
        )

    pause()


def show_entity_by_id(
    entity: str,
) -> None:
    while True:
        item_id = ask_int(
            "Keresett ID (0 = vissza)",
            0,
        )

        if item_id == 0:
            return

        status, payload = api.request(
            "GET",
            entity_path(
                entity,
                item_id,
            ),
        )

        if status == 404:
            failure(
                "Nincs ilyen ID. "
                "Próbáld újra."
            )
            continue

        header(
            f"Részletes találat — "
            f"{ENTITY_NAMES[entity]}"
        )

        if status != 200:
            failure(
                f"HTTP {status}: "
                f"{error_message(payload)}"
            )

        else:
            print_entity_detail(
                entity,
                payload,
            )

        pause()
        return


def search_mode_menu(
    entity: str,
) -> None:
    while True:
        header(
            f"Keresés — {ENTITY_NAMES[entity]}"
        )

        print(
            "  1. Teljes lista / "
            "szöveges szűrés"
        )

        print(
            "  2. Keresés pontos "
            "ID alapján"
        )

        print(
            "  0. Vissza\n"
        )

        choice = ask_int(
            "Választás",
            0,
            2,
        )

        if choice == 0:
            return

        if choice == 1:
            show_entity_list(
                entity
            )

        else:
            show_entity_by_id(
                entity
            )


def search_menu() -> None:
    options = {
        1:
            "persons",

        2:
            "vehicles",

        3:
            "spots",

        4:
            "blacklist",

        5:
            "reservations",
    }

    while True:
        header(
            "3. Keresés"
        )

        print(
            "  1. Személyek"
        )

        print(
            "  2. Járművek"
        )

        print(
            "  3. Parkolóhelyek"
        )

        print(
            "  4. Tiltólista"
        )

        print(
            "  5. Foglalások"
        )

        print(
            "  0. Vissza\n"
        )

        choice = ask_int(
            "Választás",
            0,
            5,
        )

        if choice == 0:
            return

        search_mode_menu(
            options[choice]
        )


def cancel_reservation() -> None:
    while True:
        header(
            "4. Foglalás lemondása"
        )

        reservation_id = ask_int(
            "Foglalás ID (0 = vissza)",
            0,
        )

        if reservation_id == 0:
            return

        status, reservation = api.request(
            "GET",
            f"/reservation/{reservation_id}",
        )

        if status == 404:
            failure(
                "Nincs ilyen foglalás. "
                "Próbáld újra."
            )
            pause()
            continue

        if status != 200:
            failure(
                f"HTTP {status}: "
                f"{error_message(reservation)}"
            )
            pause()
            continue

        header(
            "Foglalás lemondása — összegzés"
        )

        print_entity_detail(
            "reservations",
            reservation,
        )

        print(
            "\n  1. Foglalás lemondása"
        )

        print(
            "  0. Vissza\n"
        )

        choice = ask_int(
            "Választás",
            0,
            1,
        )

        if choice == 0:
            continue

        if not confirm(
            "Biztosan lemondod ezt a foglalást?"
        ):
            warning(
                "Lemondás megszakítva."
            )
            pause()
            continue

        status, payload = api.request(
            "DELETE",
            f"/reservation/{reservation_id}",
        )

        print()

        if status == 204:
            success(
                "A foglalást sikeresen "
                "lemondtad."
            )

        else:
            failure(
                f"HTTP {status}: "
                f"{error_message(payload)}"
            )

        pause()


def main_menu() -> None:
    while True:
        header()

        print(
            "  1. Foglalás kérése"
        )

        print(
            "  2. Adatgenerálás"
        )

        print(
            "  3. Keresés"
        )

        print(
            "  4. Foglalás lemondása"
        )

        print(
            "  0. Kilépés\n"
        )

        choice = ask_int(
            "Választás",
            0,
            4,
        )

        if choice == 1:
            reservation_menu()

        elif choice == 2:
            generate_data()

        elif choice == 3:
            search_menu()

        elif choice == 4:
            cancel_reservation()

        else:
            clear()

            print(
                f"{GREEN}{BOLD}"
                "Viszlát!"
                f"{RESET}"
            )

            return


def main() -> None:
    ensure_api()
    main_menu()


if __name__ == "__main__":
    main()
