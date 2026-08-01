/*
  ____  _                _                  _ __  __ _ _                     _    ___ 
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____     _       / \  |_ _|
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \  _| |_    / _ \  | | 
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/ |_   _|  / ___ \ | | 
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|   |_|   /_/   \_\___|
                  |_|                                                                 
*/



package hu.shepherdmike.parkolo.service;


import hu.shepherdmike.parkolo.entity.Foglalas;
import hu.shepherdmike.parkolo.entity.Jarmu;
import hu.shepherdmike.parkolo.entity.Kategoria;
import hu.shepherdmike.parkolo.entity.Parkolohely;
import hu.shepherdmike.parkolo.entity.Tiltas;
import hu.shepherdmike.parkolo.entity.Tulajdonos;
import hu.shepherdmike.parkolo.repository.FoglalasRepository;
import hu.shepherdmike.parkolo.repository.JarmuRepository;
import hu.shepherdmike.parkolo.repository.KategoriaRepository;
import hu.shepherdmike.parkolo.repository.ParkolohelyRepository;
import hu.shepherdmike.parkolo.repository.TiltasRepository;
import hu.shepherdmike.parkolo.repository.TulajdonosRepository;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;


@Service
public class TesztadatService {

  private static final String[] KERESZTNEVEK = {
      "Anna",
      "Bence",
      "Csaba",
      "Dora",
      "Emese",
      "Ferenc",
      "Gabor",
      "Hanna",
      "Istvan",
      "Julia",
      "Katalin",
      "Laszlo",
      "Miklos",
      "Noemi",
      "Peter",
      "Reka",
      "Sandor",
      "Tamas",
      "Viktor",
      "Zoltan"
  };


  private static final String[] CSALADNEVEK = {
      "Nagy",
      "Kovacs",
      "Szabo",
      "Toth",
      "Horvath",
      "Varga",
      "Kiss",
      "Molnar",
      "Nemeth",
      "Farkas",
      "Balogh",
      "Papp",
      "Lakatos",
      "Takacs",
      "Juhasz"
  };


  private static final String[] TILTASI_OKOK = {
      "nem fizette ki a parkolasi dijat",
      "ismetelten tulhuzta a foglalasi idot",
      "engedely nelkul dohanyzott",
      "mas parkolohelyet hasznalt",
      "karokozas tortent",
      "tobbszori meg nem jelenes"
  };


  private final KategoriaRepository kategoriaRepository;
  private final TulajdonosRepository tulajdonosRepository;
  private final JarmuRepository jarmuRepository;
  private final ParkolohelyRepository parkolohelyRepository;
  private final TiltasRepository tiltasRepository;
  private final FoglalasRepository foglalasRepository;


  public TesztadatService(
      KategoriaRepository kategoriaRepository,
      TulajdonosRepository tulajdonosRepository,
      JarmuRepository jarmuRepository,
      ParkolohelyRepository parkolohelyRepository,
      TiltasRepository tiltasRepository,
      FoglalasRepository foglalasRepository
  ) {
    this.kategoriaRepository = kategoriaRepository;
    this.tulajdonosRepository = tulajdonosRepository;
    this.jarmuRepository = jarmuRepository;
    this.parkolohelyRepository = parkolohelyRepository;
    this.tiltasRepository = tiltasRepository;
    this.foglalasRepository = foglalasRepository;
  }


  @Transactional
  public GenerateDataResponse generate(
      GenerateDataRequest request
  ) {
    String batchId = Long
        .toString(
            System.currentTimeMillis(),
            36
        )
        .toUpperCase(Locale.ROOT);

    List<Kategoria> kategoriak = ensureCategories();

    List<Tulajdonos> tulajdonosok =
        createPersons(
            request.persons()
        );

    List<Jarmu> jarmuvek =
        createVehicles(
            tulajdonosok,
            kategoriak,
            request.vehiclesPerPerson(),
            batchId
        );

    List<Parkolohely> ujParkolohelyek =
        createSpots(
            kategoriak,
            request.spotsPerCategory(),
            batchId
        );

    List<Tiltas> tiltasok =
        createBlacklists(
            tulajdonosok,
            request.blacklistPercent()
        );

    List<Parkolohely> hasznalhatoParkolohelyek =
        ujParkolohelyek.isEmpty()
            ? parkolohelyRepository.findAllByOrderByIdAsc()
            : ujParkolohelyek;

    List<Foglalas> foglalasok =
        createPastReservations(
            jarmuvek,
            hasznalhatoParkolohelyek,
            request.pastReservations()
        );

    return new GenerateDataResponse(
        batchId,
        kategoriak.size(),
        tulajdonosok.size(),
        jarmuvek.size(),
        ujParkolohelyek.size(),
        tiltasok.size(),
        foglalasok.size()
    );
  }


  private List<Kategoria> ensureCategories() {
    List<Kategoria> kategoriak =
        new ArrayList<>(
            kategoriaRepository.findAll()
        );

    if (kategoriak.isEmpty()) {
      kategoriak = kategoriaRepository.saveAll(
          List.of(
              new Kategoria(
                  "Motorbicikli",
                  1
              ),
              new Kategoria(
                  "Szemelyauto",
                  2
              ),
              new Kategoria(
                  "Teherauto",
                  3
              )
          )
      );
    }

    kategoriak.sort(
        Comparator.comparing(
            Kategoria::getMeretSorrend
        )
    );

    return kategoriak;
  }


  private List<Tulajdonos> createPersons(
      int count
  ) {
    ThreadLocalRandom random =
        ThreadLocalRandom.current();

    List<Tulajdonos> tulajdonosok =
        new ArrayList<>();

    for (int i = 0; i < count; i++) {
      String keresztnev =
          KERESZTNEVEK[
              random.nextInt(
                  KERESZTNEVEK.length
              )
          ];

      String csaladnev =
          CSALADNEVEK[
              random.nextInt(
                  CSALADNEVEK.length
              )
          ];

      Long specialisStatusId = null;

      tulajdonosok.add(
          new Tulajdonos(
              keresztnev,
              csaladnev,
              specialisStatusId
          )
      );
    }

    return tulajdonosRepository.saveAll(
        tulajdonosok
    );
  }


  private List<Jarmu> createVehicles(
      List<Tulajdonos> tulajdonosok,
      List<Kategoria> kategoriak,
      int vehiclesPerPerson,
      String batchId
  ) {
    ThreadLocalRandom random =
        ThreadLocalRandom.current();

    List<Jarmu> jarmuvek =
        new ArrayList<>();

    int index = 1;

    for (Tulajdonos tulajdonos : tulajdonosok) {
      for (
          int i = 0;
          i < vehiclesPerPerson;
          i++
      ) {
        Kategoria kategoria =
            kategoriak.get(
                random.nextInt(
                    kategoriak.size()
                )
            );

        String rendszam =
            "TEST-"
                + batchId
                + "-"
                + String.format(
                    "%05d",
                    index
                );

        jarmuvek.add(
            new Jarmu(
                tulajdonos,
                kategoria,
                rendszam
            )
        );

        index++;
      }
    }

    return jarmuRepository.saveAll(
        jarmuvek
    );
  }


  private List<Parkolohely> createSpots(
      List<Kategoria> kategoriak,
      int spotsPerCategory,
      String batchId
  ) {
    List<Parkolohely> parkolohelyek =
        new ArrayList<>();

    for (Kategoria kategoria : kategoriak) {
      for (
          int i = 1;
          i <= spotsPerCategory;
          i++
      ) {
        String identifier =
            "GEN-"
                + batchId
                + "-"
                + kategoria.getId()
                + "-"
                + i;

        parkolohelyek.add(
            new Parkolohely(
                identifier,
                true,
                kategoria
            )
        );
      }
    }

    return parkolohelyRepository.saveAll(
        parkolohelyek
    );
  }


  private List<Tiltas> createBlacklists(
      List<Tulajdonos> tulajdonosok,
      int blacklistPercent
  ) {
    int blacklistCount =
        (int) Math.round(
            tulajdonosok.size()
                * blacklistPercent
                / 100.0
        );

    if (blacklistCount == 0) {
      return List.of();
    }

    ThreadLocalRandom random =
        ThreadLocalRandom.current();

    List<Tulajdonos> shuffled =
        new ArrayList<>(
            tulajdonosok
        );

    Collections.shuffle(shuffled);

    OffsetDateTime now =
        OffsetDateTime.now(
            ZoneOffset.UTC
        );

    List<Tiltas> tiltasok =
        new ArrayList<>();

    for (
        int i = 0;
        i < blacklistCount;
        i++
    ) {
      boolean active =
          i % 2 == 0;

      OffsetDateTime start =
          now.minusDays(
              random.nextLong(
                  30,
                  366
              )
          );

      OffsetDateTime end;

      if (active) {
        end =
            i % 3 == 0
                ? null
                : now.plusDays(
                    random.nextLong(
                        7,
                        91
                    )
                );
      }
      else {
        end =
            start.plusDays(
                random.nextLong(
                    1,
                    15
                )
            );
      }

      String reason =
          TILTASI_OKOK[
              random.nextInt(
                  TILTASI_OKOK.length
              )
          ];

      tiltasok.add(
          new Tiltas(
              shuffled.get(i),
              reason,
              start,
              end,
              active
          )
      );
    }

    return tiltasRepository.saveAll(
        tiltasok
    );
  }


  private List<Foglalas> createPastReservations(
      List<Jarmu> jarmuvek,
      List<Parkolohely> parkolohelyek,
      int requestedCount
  ) {
    if (
        requestedCount == 0
            || jarmuvek.isEmpty()
            || parkolohelyek.isEmpty()
    ) {
      return List.of();
    }

    OffsetDateTime baseEnd =
        OffsetDateTime
            .now(ZoneOffset.UTC)
            .minusYears(10);

    List<Foglalas> foglalasok =
        new ArrayList<>();

    for (
        int i = 0;
        i < requestedCount;
        i++
    ) {
      Jarmu jarmu =
          jarmuvek.get(
              i % jarmuvek.size()
          );

      List<Parkolohely> compatibleSpots =
          parkolohelyek
              .stream()
              .filter(spot ->
                  spot
                      .getKategoria()
                      .getMeretSorrend()
                      >= jarmu
                          .getKategoria()
                          .getMeretSorrend()
              )
              .toList();

      if (compatibleSpots.isEmpty()) {
        continue;
      }

      Parkolohely parkolohely =
          compatibleSpots.get(
              i % compatibleSpots.size()
          );

      OffsetDateTime end =
          baseEnd.minusHours(
              i * 2L
          );

      OffsetDateTime start =
          end.minusHours(1);

      OffsetDateTime created =
          start.minusHours(1);

      foglalasok.add(
          new Foglalas(
              jarmu,
              parkolohely,
              start,
              end,
              created
          )
      );
    }

    return foglalasRepository.saveAll(
        foglalasok
    );
  }


  public record GenerateDataRequest(

      @NotNull(message = "a persons mezo kotelezo")
      @Min(value = 1, message = "legalabb 1 szemely kell")
      @Max(value = 1000, message = "maximum 1000 szemely generalhato")
      Integer persons,

      @NotNull(message = "a vehiclesPerPerson mezo kotelezo")
      @Min(value = 1, message = "legalabb 1 jarmu kell szemelyenkent")
      @Max(value = 5, message = "maximum 5 jarmu lehet szemelyenkent")
      Integer vehiclesPerPerson,

      @NotNull(message = "a spotsPerCategory mezo kotelezo")
      @Min(value = 0, message = "a parkolohelyek szama nem lehet negativ")
      @Max(value = 200, message = "maximum 200 hely generalhato kategoriankent")
      Integer spotsPerCategory,

      @NotNull(message = "a pastReservations mezo kotelezo")
      @Min(value = 0, message = "a foglalasok szama nem lehet negativ")
      @Max(value = 5000, message = "maximum 5000 foglalas generalhato")
      Integer pastReservations,

      @NotNull(message = "a blacklistPercent mezo kotelezo")
      @Min(value = 0, message = "a szazalek legalabb 0")
      @Max(value = 100, message = "a szazalek maximum 100")
      Integer blacklistPercent
  ) {
  }


  public record GenerateDataResponse(
      String batchId,
      int categoriesUsed,
      int personsCreated,
      int vehiclesCreated,
      int spotsCreated,
      int blacklistsCreated,
      int pastReservationsCreated
  ) {
  }
}
