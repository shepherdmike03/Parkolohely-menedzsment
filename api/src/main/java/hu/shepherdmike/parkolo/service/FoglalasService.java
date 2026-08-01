/*  
  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/



package hu.shepherdmike.parkolo.service;

import hu.shepherdmike.parkolo.exception.ApiConflictException;
import hu.shepherdmike.parkolo.dto.BlacklistReasonResponse;
import hu.shepherdmike.parkolo.dto.FreeSpotsResponse;
import hu.shepherdmike.parkolo.dto.NewReservationAcceptedResponse;
import hu.shepherdmike.parkolo.dto.NewReservationRequest;
import hu.shepherdmike.parkolo.dto.ReservationDetailsResponse;
import hu.shepherdmike.parkolo.dto.ReservationListResponse;
import hu.shepherdmike.parkolo.dto.SpotResponse;
import hu.shepherdmike.parkolo.dto.SpotStatusResponse;
import hu.shepherdmike.parkolo.entity.Foglalas;
import hu.shepherdmike.parkolo.entity.Jarmu;
import hu.shepherdmike.parkolo.entity.Parkolohely;
import hu.shepherdmike.parkolo.entity.Tiltas;
import hu.shepherdmike.parkolo.exception.ReservationRejectedException;
import hu.shepherdmike.parkolo.exception.ResourceNotFoundException;
import hu.shepherdmike.parkolo.repository.FoglalasRepository;
import hu.shepherdmike.parkolo.repository.JarmuRepository;
import hu.shepherdmike.parkolo.repository.ParkolohelyRepository;
import hu.shepherdmike.parkolo.repository.TiltasRepository;


/*annotaciok*/
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/*ido*/
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;





/*konstruktor*/
@Service
public class FoglalasService {

  private final FoglalasRepository foglalasRepository;
  private final JarmuRepository jarmuRepository;
  private final ParkolohelyRepository parkolohelyRepository;
  private final TiltasRepository tiltasRepository;



  public FoglalasService(
      FoglalasRepository foglalasRepository,
      JarmuRepository jarmuRepository,
      ParkolohelyRepository parkolohelyRepository,
      TiltasRepository tiltasRepository
      ) 
  {
    this.foglalasRepository = foglalasRepository;
    this.jarmuRepository = jarmuRepository;
    this.parkolohelyRepository = parkolohelyRepository;
    this.tiltasRepository = tiltasRepository;
  }




  /*Uj foglalas*/
  @Transactional
  public NewReservationAcceptedResponse createReservation(
      NewReservationRequest request
      ) 
  {
    validateInterval(
        request.kezdetIdo(),
        request.vegIdo()
        );

    OffsetDateTime most = OffsetDateTime.now(ZoneOffset.UTC);

    if (request.kezdetIdo().isBefore(most)) {
      throw new IllegalArgumentException(
          "a foglalas kezdete nem lehet multban"
          );
    }



    Jarmu jarmu =findVehicle(request.jarmuId());



    List<Tiltas> aktivTiltasok =
      tiltasRepository.findActiveByOwnerId(
          jarmu.getTulajdonos().getId(),
          most
          );



      // ha nincs tiltas
    if (!aktivTiltasok.isEmpty()) {
      List<BlacklistReasonResponse> tiltasok= aktivTiltasok
        .stream()
        .map(BlacklistReasonResponse::fromEntity)
        .toList();



      throw new ReservationRejectedException(
          HttpStatus.FORBIDDEN,
          "TULAJDONOS_TILTOTT",
          "a tulajdonos aktiv tiltas alatt all",
          tiltasok
          );
    }



          // jarmuvek utkozese
    long jarmuUtkozesek =
      foglalasRepository.countVehicleOverlaps(
          jarmu.getId(),
          request.kezdetIdo(),
          request.vegIdo()
          );



    if (jarmuUtkozesek > 0) {
      throw new ReservationRejectedException(
          HttpStatus.CONFLICT,
          "JARMU_MAR_FOGLALT",
          "mar van atfedo foglalas",
          List.of()
          );
    }



    Parkolohely parkolohely = parkolohelyRepository
      .findBestAvailableSpotForUpdate(
          jarmu.getKategoria().getMeretSorrend(),
          request.kezdetIdo(),
          request.vegIdo()
          )
      .orElseThrow(() -> new ReservationRejectedException(
            HttpStatus.CONFLICT,
            "NINCS_MEGFELELO_HELY",
            "nincs szabad hely erre a jarmure",
            List.of()
            ));



    // sikeres foglalas
    Foglalas foglalas = new Foglalas(
        jarmu,
        parkolohely,
        request.kezdetIdo(),
        request.vegIdo()
        );



    try {
      Foglalas mentettFoglalas =
        foglalasRepository.saveAndFlush(foglalas);

      return new NewReservationAcceptedResponse(
          true,
          ReservationDetailsResponse.fromEntity(
            mentettFoglalas
            )
          );

    } catch (DataIntegrityViolationException exception) {
      throw new ReservationRejectedException(
          HttpStatus.CONFLICT,
          "FOGLALASI_UTKOZES",
          "utkozes tortent"
          + "probaljad megfele ujra",
          List.of()
          );
    }
  }


      // csak olvas     SZABAD HELYEKET
  @Transactional(readOnly = true)
  public FreeSpotsResponse listFreeSpots(
      OffsetDateTime kezdetIdo,
      OffsetDateTime vegIdo,
      Long jarmuId
      ) 
  {
    validateInterval(kezdetIdo, vegIdo);

    List<Parkolohely> helyek;



    if (jarmuId == null) {
      helyek = parkolohelyRepository.findFreeSpots(
          kezdetIdo,
          vegIdo
          );
    } 
    else 
    {
      Jarmu jarmu = findVehicle(jarmuId);


      helyek =
        parkolohelyRepository.findCompatibleFreeSpots(
            jarmu.getKategoria().getMeretSorrend(),
            kezdetIdo,
            vegIdo
            );
    }



    List<SpotResponse> responseHelyek = helyek
      .stream()
      .map(SpotResponse::fromEntity)
      .toList();



    return new FreeSpotsResponse(
        kezdetIdo,
        vegIdo,
        jarmuId,
        responseHelyek.size(),
        responseHelyek
        );

  }





    // CSAK OLVAS Statusz helyek
  @Transactional(readOnly = true)
  public SpotStatusResponse getSpotStatus(
      OffsetDateTime kezdetIdo,
      OffsetDateTime vegIdo
      ) 
  {
    validateInterval(kezdetIdo, vegIdo);



    long osszesAktivHely =
      parkolohelyRepository.countByAktivTrue();



    long foglaltHely =
      foglalasRepository.countOccupiedSpots(
          kezdetIdo,
          vegIdo
          );




    long szabadHely = Math.max(
        0,
        osszesAktivHely - foglaltHely
        );



    return new SpotStatusResponse(
        kezdetIdo,
        vegIdo,
        osszesAktivHely,
        foglaltHely,
        szabadHely
        );
  }



  /*Listazzuk a rezervaciokat*/
  @Transactional(readOnly = true)
  public ReservationListResponse listReservations() {
    List<ReservationDetailsResponse> foglalasok =
      foglalasRepository
      .findAllDetailed()
      .stream()
      .map(ReservationDetailsResponse::fromEntity)
      .toList();




    return new ReservationListResponse(
        foglalasok.size(),
        foglalasok
        );
  }


  @Transactional
  public void cancelReservation(
      Long foglalasId
  ) {
    Foglalas foglalas = foglalasRepository
        .findDetailedById(foglalasId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "nem letezik foglalas ezzel az ID-val: "
                + foglalasId
       ));

    OffsetDateTime now =
        OffsetDateTime.now(
           ZoneOffset.UTC
       );
  
    if (
        foglalas.getErkezesIdo() != null
            || !now.isBefore(
                foglalas.getKezdetIdo()
            )
    ) {
      throw new ApiConflictException(
          "csak meg el nem kezdodott foglalas mondhato le"
      );
   }
  
    foglalasRepository.delete(
        foglalas
    );
  }

  /*Rakeresni egy bizonyos foglalasra*/
  @Transactional(readOnly = true)
  public ReservationDetailsResponse getReservation(
      Long foglalasId
      ) 
  {
    
    Foglalas foglalas = foglalasRepository
      .findDetailedById(foglalasId)
      .orElseThrow(() -> new ResourceNotFoundException(
            "nem letezik foglalas ezzel: "
            + foglalasId
            ));

    return ReservationDetailsResponse.fromEntity(foglalas);
      
  }




  private Jarmu findVehicle(Long jarmuId) {
    return jarmuRepository
      .findDetailedById(jarmuId)
      .orElseThrow(() -> new ResourceNotFoundException(
            "nem letezik jarmu ezzel az ID val:"
            + jarmuId
            ));
  }




  private void validateInterval(
      OffsetDateTime kezdetIdo,
      OffsetDateTime vegIdo
      ) 
  {
    if (kezdetIdo == null || vegIdo == null) {
      throw new IllegalArgumentException(
          "kezdo es veg ido megadasa kotelezo"
          );
    }



    if (!vegIdo.isAfter(kezdetIdo)) {
      throw new IllegalArgumentException(
          "a vegido a kezdoido utaninak kell lennie"
          );
    }
  }
}




