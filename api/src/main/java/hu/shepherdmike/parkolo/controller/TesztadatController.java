/*
  ____  _                _                  _ __  __ _ _                     _    ___ 
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____     _       / \  |_ _|
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \  _| |_    / _ \  | | 
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/ |_   _|  / ___ \ | | 
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|   |_|   /_/   \_\___|
                  |_|                                                                 
*/




package hu.shepherdmike.parkolo.controller;


import hu.shepherdmike.parkolo.service.TesztadatService;
import hu.shepherdmike.parkolo.service.TesztadatService.GenerateDataRequest;
import hu.shepherdmike.parkolo.service.TesztadatService.GenerateDataResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TesztadatController {

  private final TesztadatService tesztadatService;


  public TesztadatController(
      TesztadatService tesztadatService
  ) {
    this.tesztadatService = tesztadatService;
  }


  @PostMapping("/dev/generate_data")
  public ResponseEntity<GenerateDataResponse> generateData(
      @Valid
      @RequestBody
      GenerateDataRequest request
  ) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(
            tesztadatService.generate(request)
        );
  }
}
