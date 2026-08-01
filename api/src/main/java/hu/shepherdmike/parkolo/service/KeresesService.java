/*
  ____  _                _                  _ __  __ _ _                     _    ___ 
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____     _       / \  |_ _|
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \  _| |_    / _ \  | | 
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/ |_   _|  / ___ \ | | 
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|   |_|   /_/   \_\___|
                  |_|                                                                 
*/


package hu.shepherdmike.parkolo.service;


import hu.shepherdmike.parkolo.entity.Jarmu;
import hu.shepherdmike.parkolo.entity.Parkolohely;
import hu.shepherdmike.parkolo.entity.Tiltas;
import hu.shepherdmike.parkolo.entity.Tulajdonos;
import hu.shepherdmike.parkolo.exception.ResourceNotFoundException;
import hu.shepherdmike.parkolo.repository.JarmuRepository;
import hu.shepherdmike.parkolo.repository.ParkolohelyRepository;
import hu.shepherdmike.parkolo.repository.TiltasRepository;
import hu.shepherdmike.parkolo.repository.TulajdonosRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;


@Service
public class KeresesService {

  private final TulajdonosRepository tulajdonosRepository;
  private final JarmuRepository jarmuRepository;
  private final ParkolohelyRepository parkolohelyRepository;
  private final TiltasRepository tiltasRepository;


  public KeresesService(
      TulajdonosRepository tulajdonosRepository,
      JarmuRepository jarmuRepository,
      ParkolohelyRepository parkolohelyRepository,
      TiltasRepository tiltasRepository
  ) {
    this.tulajdonosRepository = tulajdonosRepository;
    this.jarmuRepository = jarmuRepository;
    this.parkolohelyRepository = parkolohelyRepository;
    this.tiltasRepository = tiltasRepository;
  }


  @Transactional(readOnly = true)
  public List<PersonResult> searchPersons(String query) {
    String normalizedQuery = normalize(query);

    return tulajdonosRepository
        .findAllByOrderByIdAsc()
        .stream()
        .map(PersonResult::fromEntity)
        .filter(person ->
            normalizedQuery.isBlank()
                || contains(person.nev(), normalizedQuery)
                || contains(
                    String.valueOf(person.tulajdonosId()),
                    normalizedQuery
                )
        )
        .toList();
  }


  @Transactional(readOnly = true)
  public PersonResult getPerson(Long id) {
    Tulajdonos tulajdonos = tulajdonosRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            "nem letezik tulajdonos ezzel az ID-val: " + id
        ));

    return PersonResult.fromEntity(tulajdonos);
  }


  @Transactional(readOnly = true)
  public List<VehicleResult> searchVehicles(String query) {
    String normalizedQuery = normalize(query);

    return jarmuRepository
        .findAllDetailed()
        .stream()
        .map(VehicleResult::fromEntity)
        .filter(vehicle ->
            normalizedQuery.isBlank()
                || contains(vehicle.rendszam(), normalizedQuery)
                || contains(vehicle.tulajdonosNev(), normalizedQuery)
                || contains(vehicle.kategoriaNev(), normalizedQuery)
                || contains(
                    String.valueOf(vehicle.jarmuId()),
                    normalizedQuery
                )
        )
        .toList();
  }


  @Transactional(readOnly = true)
  public VehicleResult getVehicle(Long id) {
    Jarmu jarmu = jarmuRepository
        .findDetailedById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            "nem letezik jarmu ezzel az ID-val: " + id
        ));

    return VehicleResult.fromEntity(jarmu);
  }


  @Transactional(readOnly = true)
  public List<SpotResult> searchSpots(String query) {
    String normalizedQuery = normalize(query);

    return parkolohelyRepository
        .findAllByOrderByIdAsc()
        .stream()
        .map(SpotResult::fromEntity)
        .filter(spot ->
            normalizedQuery.isBlank()
                || contains(spot.helyAzonosito(), normalizedQuery)
                || contains(spot.kategoriaNev(), normalizedQuery)
                || contains(
                    String.valueOf(spot.parkolohelyId()),
                    normalizedQuery
                )
        )
        .toList();
  }


  @Transactional(readOnly = true)
  public SpotResult getSpot(Long id) {
    Parkolohely parkolohely = parkolohelyRepository
        .findDetailedById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            "nem letezik parkolohely ezzel az ID-val: " + id
        ));

    return SpotResult.fromEntity(parkolohely);
  }


  @Transactional(readOnly = true)
  public List<BlacklistResult> searchBlacklist(String query) {
    String normalizedQuery = normalize(query);
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

    return tiltasRepository
        .findAllDetailed()
        .stream()
        .map(tiltas -> BlacklistResult.fromEntity(tiltas, now))
        .filter(tiltas ->
            normalizedQuery.isBlank()
                || contains(tiltas.tulajdonosNev(), normalizedQuery)
                || contains(tiltas.ok(), normalizedQuery)
                || contains(
                    String.valueOf(tiltas.tiltasId()),
                    normalizedQuery
                )
                || contains(
                    String.valueOf(tiltas.tulajdonosId()),
                    normalizedQuery
                )
        )
        .toList();
  }


  @Transactional(readOnly = true)
  public BlacklistResult getBlacklistEntry(Long id) {
    Tiltas tiltas = tiltasRepository
        .findDetailedById(id)
        .orElseThrow(() -> new ResourceNotFoundException(
            "nem letezik tiltas ezzel az ID-val: " + id
        ));

    return BlacklistResult.fromEntity(
        tiltas,
        OffsetDateTime.now(ZoneOffset.UTC)
    );
  }


  private String normalize(String value) {
    if (value == null) {
      return "";
    }

    return value
        .trim()
        .toLowerCase(Locale.ROOT);
  }


  private boolean contains(
      String value,
      String normalizedQuery
  ) {
    return value != null
        && value
            .toLowerCase(Locale.ROOT)
            .contains(normalizedQuery);
  }


  public record PersonResult(
      Long tulajdonosId,
      String nev,
      Long specialisStatusId
  ) {
    public static PersonResult fromEntity(
        Tulajdonos tulajdonos
    ) {
      return new PersonResult(
          tulajdonos.getId(),
          tulajdonos.getTeljesNev(),
          tulajdonos.getSpecialisStatusId()
      );
    }
  }


  public record VehicleResult(
      Long jarmuId,
      String rendszam,

      Long tulajdonosId,
      String tulajdonosNev,

      Long kategoriaId,
      String kategoriaNev
  ) {
    public static VehicleResult fromEntity(
        Jarmu jarmu
    ) {
      return new VehicleResult(
          jarmu.getId(),
          jarmu.getRendszam(),

          jarmu.getTulajdonos().getId(),
          jarmu.getTulajdonos().getTeljesNev(),

          jarmu.getKategoria().getId(),
          jarmu.getKategoria().getNev()
      );
    }
  }


  public record SpotResult(
      Long parkolohelyId,
      String helyAzonosito,
      boolean aktiv,

      Long kategoriaId,
      String kategoriaNev
  ) {
    public static SpotResult fromEntity(
        Parkolohely parkolohely
    ) {
      return new SpotResult(
          parkolohely.getId(),
          parkolohely.getHelyAzonosito(),
          parkolohely.isAktiv(),

          parkolohely.getKategoria().getId(),
          parkolohely.getKategoria().getNev()
      );
    }
  }


  public record BlacklistResult(
      Long tiltasId,

      Long tulajdonosId,
      String tulajdonosNev,

      String ok,
      OffsetDateTime tiltasKezdete,
      OffsetDateTime tiltasVege,

      boolean aktiv
  ) {
    public static BlacklistResult fromEntity(
        Tiltas tiltas,
        OffsetDateTime now
    ) {
      boolean currentlyActive =
          tiltas.isAktiv()
              && (
                  tiltas.getTiltasVege() == null
                      || tiltas.getTiltasVege().isAfter(now)
              );

      return new BlacklistResult(
          tiltas.getId(),

          tiltas.getTulajdonos().getId(),
          tiltas.getTulajdonos().getTeljesNev(),

          tiltas.getTiltasOk(),
          tiltas.getTiltasKezdete(),
          tiltas.getTiltasVege(),

          currentlyActive
      );
    }
  }
}
