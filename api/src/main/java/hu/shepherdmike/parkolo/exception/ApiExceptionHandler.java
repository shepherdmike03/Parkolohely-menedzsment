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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;     /*Map - hez*/


@RestControllerAdvice
public class ApiExceptionHandler {



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
}
