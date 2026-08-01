/*
  ____  _                _                  _ __  __ _ _                     _    ___ 
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____     _       / \  |_ _|
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \  _| |_    / _ \  | | 
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/ |_   _|  / ___ \ | | 
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|   |_|   /_/   \_\___|
                  |_|                                                                 
*/

package hu.shepherdmike.parkolo.controller;


import hu.shepherdmike.parkolo.service.KeresesService;
import hu.shepherdmike.parkolo.service.KeresesService.BlacklistResult;
import hu.shepherdmike.parkolo.service.KeresesService.PersonResult;
import hu.shepherdmike.parkolo.service.KeresesService.SpotResult;
import hu.shepherdmike.parkolo.service.KeresesService.VehicleResult;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/search")
public class KeresesController {

  private final KeresesService keresesService;


  public KeresesController(
      KeresesService keresesService
  ) {
    this.keresesService = keresesService;
  }


  @GetMapping("/persons")
  public ResponseEntity<List<PersonResult>> searchPersons(
      @RequestParam(
          name = "q",
          required = false,
          defaultValue = ""
      )
      String query
  ) {
    return ResponseEntity.ok(
        keresesService.searchPersons(query)
    );
  }


  @GetMapping("/persons/{id}")
  public ResponseEntity<PersonResult> getPerson(
      @PathVariable Long id
  ) {
    return ResponseEntity.ok(
        keresesService.getPerson(id)
    );
  }


  @GetMapping("/vehicles")
  public ResponseEntity<List<VehicleResult>> searchVehicles(
      @RequestParam(
          name = "q",
          required = false,
          defaultValue = ""
      )
      String query
  ) {
    return ResponseEntity.ok(
        keresesService.searchVehicles(query)
    );
  }


  @GetMapping("/vehicles/{id}")
  public ResponseEntity<VehicleResult> getVehicle(
      @PathVariable Long id
  ) {
    return ResponseEntity.ok(
        keresesService.getVehicle(id)
    );
  }


  @GetMapping("/spots")
  public ResponseEntity<List<SpotResult>> searchSpots(
      @RequestParam(
          name = "q",
          required = false,
          defaultValue = ""
      )
      String query
  ) {
    return ResponseEntity.ok(
        keresesService.searchSpots(query)
    );
  }


  @GetMapping("/spots/{id}")
  public ResponseEntity<SpotResult> getSpot(
      @PathVariable Long id
  ) {
    return ResponseEntity.ok(
        keresesService.getSpot(id)
    );
  }


  @GetMapping("/blacklist")
  public ResponseEntity<List<BlacklistResult>> searchBlacklist(
      @RequestParam(
          name = "q",
          required = false,
          defaultValue = ""
      )
      String query
  ) {
    return ResponseEntity.ok(
        keresesService.searchBlacklist(query)
    );
  }


  @GetMapping("/blacklist/{id}")
  public ResponseEntity<BlacklistResult> getBlacklistEntry(
      @PathVariable Long id
  ) {
    return ResponseEntity.ok(
        keresesService.getBlacklistEntry(id)
    );
  }
}
