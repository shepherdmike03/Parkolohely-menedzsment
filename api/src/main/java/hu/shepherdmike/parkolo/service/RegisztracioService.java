/*
  ____  _                _                  _ __  __ _ _                     _    ___ 
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____     _       / \  |_ _|
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \  _| |_    / _ \  | | 
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/ |_   _|  / ___ \ | | 
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|   |_|   /_/   \_\___|
                  |_|                                                                 
*/


package hu.shepherdmike.parkolo.service;


import hu.shepherdmike.parkolo.dto.CategoryResponse;
import hu.shepherdmike.parkolo.dto.NewCustomerVehicleRequest;
import hu.shepherdmike.parkolo.dto.NewCustomerVehicleResponse;
import hu.shepherdmike.parkolo.entity.Jarmu;
import hu.shepherdmike.parkolo.entity.Kategoria;
import hu.shepherdmike.parkolo.entity.Tulajdonos;
import hu.shepherdmike.parkolo.exception.ApiConflictException;
import hu.shepherdmike.parkolo.exception.ResourceNotFoundException;
import hu.shepherdmike.parkolo.repository.JarmuRepository;
import hu.shepherdmike.parkolo.repository.KategoriaRepository;
import hu.shepherdmike.parkolo.repository.TulajdonosRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;


@Service
public class RegisztracioService {

  private final TulajdonosRepository tulajdonosRepository;
  private final JarmuRepository jarmuRepository;
  private final KategoriaRepository kategoriaRepository;


  public RegisztracioService(
      TulajdonosRepository tulajdonosRepository,
      JarmuRepository jarmuRepository,
      KategoriaRepository kategoriaRepository
  ) {
    this.tulajdonosRepository = tulajdonosRepository;
    this.jarmuRepository = jarmuRepository;
    this.kategoriaRepository = kategoriaRepository;
  }


  @Transactional(readOnly = true)
  public List<CategoryResponse> listCategories() {

    return kategoriaRepository
        .findAll()
        .stream()
        .sorted(
            Comparator
                .comparing(Kategoria::getMeretSorrend)
                .thenComparing(Kategoria::getId)
        )
        .map(CategoryResponse::fromEntity)
        .toList();
  }


  @Transactional
  public NewCustomerVehicleResponse registerCustomerAndVehicle(
      NewCustomerVehicleRequest request
  ) {
    String keresztnev =
        request.keresztnev().trim();

    String csaladnev =
        request.csaladnev().trim();

    String rendszam =
        request.rendszam()
            .trim()
            .toUpperCase(Locale.ROOT);

    if (
        jarmuRepository.existsByRendszamIgnoreCase(
            rendszam
        )
    ) {
      throw new ApiConflictException(
          "mar letezik jarmu ezzel a rendszammal: "
              + rendszam
      );
    }

    Kategoria kategoria = kategoriaRepository
        .findById(request.kategoriaId())
        .orElseThrow(() -> new ResourceNotFoundException(
            "nem letezik kategoria ezzel az ID-val: "
                + request.kategoriaId()
        ));

    Tulajdonos tulajdonos =
        tulajdonosRepository.save(
            new Tulajdonos(
                keresztnev,
                csaladnev,
                request.specialisStatusId()
            )
        );

    Jarmu jarmu = new Jarmu(
        tulajdonos,
        kategoria,
        rendszam
    );

    try {
      jarmu = jarmuRepository.saveAndFlush(
          jarmu
      );
    }
    catch (DataIntegrityViolationException exception) {
      throw new ApiConflictException(
          "mar letezik jarmu ezzel a rendszammal: "
              + rendszam
      );
    }

    return new NewCustomerVehicleResponse(
        tulajdonos.getId(),
        tulajdonos.getTeljesNev(),

        jarmu.getId(),
        jarmu.getRendszam(),

        kategoria.getId(),
        kategoria.getNev()
    );
  }
}
