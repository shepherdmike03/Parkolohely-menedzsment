//  ____  _                _                  _ __  __ _ _
// / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
// \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
//  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
// |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
//                  |_|

package hu.shepherdmike.parkolo.controller;



import hu.shepherdmike.parkolo.dto.NewSpotRequest;
import hu.shepherdmike.parkolo.dto.NewSpotResponse;
import hu.shepherdmike.parkolo.dto.SpotListResponse;
import hu.shepherdmike.parkolo.service.ParkolohelyService;


import jakarta.validation.Valid;

// szukseges importok ahoz hogy az API fogadni tudja az endpointokat
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;








@RestController

public class ParkolohelyController {


  private final ParkolohelyService parkolohelyService;


  public ParkolohelyController(
      ParkolohelyService parkolohelyService
      ) 
  {
      this.parkolohelyService = parkolohelyService;
  }


                                  
    @PostMapping("/new_spot")                   // uj parkolohely letrehozasa
    public ResponseEntity<NewSpotResponse> createSpots(
        @Valid @RequestBody NewSpotRequest request
    ) 
    {
      NewSpotResponse response = parkolohelyService.createSpots(request);

      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }




            // listazzuk a helyeket
    @GetMapping("/list_spots")
    
    public ResponseEntity<SpotListResponse> listSpots() {
      return ResponseEntity.ok(
        parkolohelyService.listSpots()
      );
    }



}
