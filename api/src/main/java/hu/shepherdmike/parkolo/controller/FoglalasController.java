/*
  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/


package hu.shepherdmike.parkolo.controller;



import hu.shepherdmike.parkolo.dto.FreeSpotsResponse;
import hu.shepherdmike.parkolo.dto.NewReservationAcceptedResponse;
import hu.shepherdmike.parkolo.dto.NewReservationRequest;
import hu.shepherdmike.parkolo.dto.ReservationDetailsResponse;
import hu.shepherdmike.parkolo.dto.ReservationListResponse;
import hu.shepherdmike.parkolo.dto.SpotStatusResponse;
import hu.shepherdmike.parkolo.service.FoglalasService;

import jakarta.validation.Valid;


/*ido + tobbi anotacio*/
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



import java.time.OffsetDateTime;


@RestController
public class FoglalasController {

  private final FoglalasService foglalasService;

  public FoglalasController(
      FoglalasService foglalasService
      ) 
  {
    this.foglalasService = foglalasService;
  }


      /*foglalas endpoint*/
  @PostMapping("/new_reservation")
  public ResponseEntity<NewReservationAcceptedResponse>
  createReservation(
      @Valid @RequestBody NewReservationRequest request
      ) 
  {
    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(
          foglalasService.createReservation(request)
          );
  }




      /*Szabad helyek keresese*/
  @GetMapping("/free_spots")
  public ResponseEntity<FreeSpotsResponse> listFreeSpots(
      @RequestParam("from")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      OffsetDateTime from,


      @RequestParam("to")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      OffsetDateTime to,


      @RequestParam(
      name = "jarmuId",
      required = false
      )
      Long jarmuId
      ) 
  {
    return ResponseEntity.ok(
        foglalasService.listFreeSpots(
          from,
          to,
          jarmuId
          )
        );
  }





    // foglalas statusza
  @GetMapping("/spot_status")
  public ResponseEntity<SpotStatusResponse> getSpotStatus(
      @RequestParam("from")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      OffsetDateTime from,



      @RequestParam("to")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      OffsetDateTime to
      ) 
  {
    return ResponseEntity.ok(
        foglalasService.getSpotStatus(from, to)
        );
  }



  @DeleteMapping("/reservation/{id}")
    public ResponseEntity<Void> cancelReservation(
      @PathVariable("id") Long id
    ) 
    {
      foglalasService.cancelReservation(id);

      return ResponseEntity
          .noContent()
          .build();
  
    }




    // listazzuk a folalasokat
  @GetMapping("/list_reservations")
  public ResponseEntity<ReservationListResponse>
  listReservations() {
    return ResponseEntity.ok(
        foglalasService.listReservations()
        );
  }



  // keressunk egy bizonyos foglalast
  @GetMapping("/reservation/{id}")
  public ResponseEntity<ReservationDetailsResponse>
  getReservation(
      @PathVariable("id") Long id
      ) 
  {
    return ResponseEntity.ok(
        foglalasService.getReservation(id)
        );
  }

}



