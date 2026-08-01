/*
  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/


package hu.shepherdmike.parkolo.controller;




import hu.shepherdmike.parkolo.dto.BlacklistPersonRequest;
import hu.shepherdmike.parkolo.dto.BlacklistPersonResponse;
import hu.shepherdmike.parkolo.service.TiltolistaService;


import jakarta.validation.Valid;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



 // Endpoint megadasa
@RestController
public class TiltolistaController {

  private final TiltolistaService tiltolistaService;

  public TiltolistaController(
      TiltolistaService tiltolistaService
      ) 
  {
    this.tiltolistaService = tiltolistaService;
  }




  @PostMapping("/blacklist_person")
  public ResponseEntity<BlacklistPersonResponse> blacklistPerson(
      @Valid @RequestBody BlacklistPersonRequest request
      ) 
  {
    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(
          tiltolistaService.blacklistPerson(request)
          );
  }
}



