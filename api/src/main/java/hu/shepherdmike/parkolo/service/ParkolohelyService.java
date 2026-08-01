//  ____  _                _                  _ __  __ _ _
// / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
// \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
//  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
// |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
//                  |_|


package hu.shepherdmike.parkolo.service;


// sajat file-ok
import hu.shepherdmike.parkolo.dto.NewSpotRequest;
import hu.shepherdmike.parkolo.dto.NewSpotResponse;
import hu.shepherdmike.parkolo.dto.SpotListResponse;
import hu.shepherdmike.parkolo.dto.SpotResponse;
import hu.shepherdmike.parkolo.entity.Kategoria;
import hu.shepherdmike.parkolo.entity.Parkolohely;

import hu.shepherdmike.parkolo.repository.KategoriaRepository;
import hu.shepherdmike.parkolo.repository.ParkolohelyRepository;


// Service + tranzakcio annotaciok
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


// utils
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;




// konSTRUKTOR
@Service
public class ParkolohelyService {
  
  private final ParkolohelyRepository parkolohelyRepository;
  private final KategoriaRepository kategoriaRepository;

  public ParkolohelyService(
      ParkolohelyRepository parkolohelyRepository,
      KategoriaRepository kategoriaRepository
      )
  {
    this.parkolohelyRepository = parkolohelyRepository;
    this.kategoriaRepository = kategoriaRepository;
  }




  @Transactional          // megkeresi az adatbazisban a kategoriat, ha nem letezik IllegalArgumentException-t dob
  public NewSpotResponse createSpots(NewSpotRequest request) {
    Kategoria kategoria = kategoriaRepository
      .findById(request.kategoriaId())
      .orElseThrow(() -> new IllegalArgumentException(        /*THROW validacio*/
            "Nem letezik kategoria ezzel az ID-val: "
            + request.kategoriaId()
            ));



            List<Parkolohely> ujHelyek = new ArrayList<>();



            // Darab mezo alapjan letrehoz darabnyi parkolohelyet
          for (int i = 0; i < request.darab(); i++) {
              String azonosito = generateSpotIdentifier(
                  request.kategoriaId()
              );




              Parkolohely parkolohely = new Parkolohely(
                  azonosito,
                  true,
                  kategoria
              );


              ujHelyek.add(parkolohely);
          }





          List<Parkolohely> mentettHelyek =
              parkolohelyRepository.saveAll(ujHelyek);      // mentes



          List<SpotResponse> responseHelyek = mentettHelyek
              .stream()
              .map(SpotResponse::fromEntity)
              .toList();

          // listazast visszateriti
        return new NewSpotResponse(
            responseHelyek.size(),
            responseHelyek
        );
    }




    @Transactional(readOnly = true)         // listazas csak olvasasi muvelet
    public SpotListResponse listSpots() {
        
      List<SpotResponse> helyek = parkolohelyRepository
          .findAllByOrderByIdAsc()
          .stream()
          .map(SpotResponse::fromEntity)
          .toList();



      return new SpotListResponse(
          helyek.size(),
          helyek
      );
    }


          // random azonosito generalo 
    private String generateSpotIdentifier(Long kategoriaId) {
      String randomPart = UUID.randomUUID()
          .toString()
          .substring(0, 8)
          .toUpperCase();

      return "K" + kategoriaId + "-" + randomPart;
    }
}




