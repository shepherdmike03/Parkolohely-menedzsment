/*
  ____  _                _                  _ __  __ _ _                     _    ___ 
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____     _       / \  |_ _|
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \  _| |_    / _ \  | | 
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/ |_   _|  / ___ \ | | 
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|   |_|   /_/   \_\___|
                  |_|                                                                 
*/

package hu.shepherdmike.parkolo.controller;


import hu.shepherdmike.parkolo.dto.CategoryResponse;
import hu.shepherdmike.parkolo.dto.NewCustomerVehicleRequest;
import hu.shepherdmike.parkolo.dto.NewCustomerVehicleResponse;
import hu.shepherdmike.parkolo.service.RegisztracioService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class RegisztracioController {

  private final RegisztracioService regisztracioService;


  public RegisztracioController(
      RegisztracioService regisztracioService
  ) {
    this.regisztracioService =
        regisztracioService;
  }


  @GetMapping("/categories")
  public ResponseEntity<List<CategoryResponse>>
  listCategories() {

    return ResponseEntity.ok(
        regisztracioService.listCategories()
    );
  }


  @PostMapping("/register_customer_vehicle")
  public ResponseEntity<NewCustomerVehicleResponse>
  registerCustomerAndVehicle(
      @Valid
      @RequestBody
      NewCustomerVehicleRequest request
  ) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(
            regisztracioService
                .registerCustomerAndVehicle(
                    request
                )
        );
  }
}
