/*  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/

// HIBAKEZELES



package hu.shepherdmike.parkolo.exception;


// anotaciok 
import hu.shepherdmike.parkolo.dto.ReservationRejectedResponse;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


import java.util.Map;     /*Map - hez*/


@RestControllerAdvice
public class ApiExceptionHandler {



      // Visszautasitott foglalas exception
  @ExceptionHandler(ReservationRejectedException.class)
    public ResponseEntity<ReservationRejectedResponse>
    handleReservationRejected(
        ReservationRejectedException exception
    ) 
    {
        return ResponseEntity
            .status(exception.getStatus())
            .body(
                new ReservationRejectedResponse(
                    false,
                    exception.getCode(),
                    exception.getMessage(),
                    exception.getTiltasok()
                )
            );
    }






          /*Nincs resource*/
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(
        ResourceNotFoundException exception
    ) 
    {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(Map.of(
                "error", exception.getMessage()
            ));
    }




        // API CONFLICT
    @ExceptionHandler(ApiConflictException.class)
    public ResponseEntity<Map<String, String>> handleConflict(
        ApiConflictException exception
    ) 
    {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(Map.of(
                "error", exception.getMessage()
            ));
    }






        // ILLEGAL ARGUMENT
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> handleIllegalArgument(
      IllegalArgumentException exception
  ) 
  {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(Map.of(
              "error", exception.getMessage()
          ));
  }




    // NOT VALID ARGUMENT
  @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
      MethodArgumentNotValidException exception
    ) 
    {
      String message = exception
          .getBindingResult()
          .getFieldErrors()
          .stream()
          .findFirst()
          .map(error -> error.getDefaultMessage())
          .orElse("a keresed ervenytelen! probald meg ujra.");



      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(Map.of(
              "error", message
          ));
    }



      /*Metodus hiba*/
   @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String,String>>
    handleTypeMismatch(
        MethodArgumentTypeMismatchException exception
    ) 
    {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                "error",
                "ervenytelen parameter"
                    + exception.getName()
            ));
    }





      // utkozes vagy hibas adat
  @ExceptionHandler(DataIntegrityViolationException.class)
   public ResponseEntity<Map<String, String>>
   handleDatabaseConflict(
        DataIntegrityViolationException exception
    )  
   {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(Map.of(
                "error",
                "muvelet elutasitva"
                    + "utkozes vagy ervenytelen adat miatt"
            ));
    
   }



}


