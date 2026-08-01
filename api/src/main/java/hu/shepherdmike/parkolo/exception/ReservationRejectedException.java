/*  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/

package hu.shepherdmike.parkolo.exception;




import hu.shepherdmike.parkolo.dto.BlacklistReasonResponse;
import org.springframework.http.HttpStatus;



import java.util.List;



public class ReservationRejectedException extends RuntimeException {

  private final HttpStatus status;
  private final String code;
  private final List<BlacklistReasonResponse> tiltasok;


  /*tiltas alapjan valo kivetel*/
  public ReservationRejectedException(
      HttpStatus status,
      String code,
      String message,
      List<BlacklistReasonResponse> tiltasok
      ) 
  {
    super(message);
    this.status = status;
    this.code = code;
    this.tiltasok = List.copyOf(tiltasok);
  }


//GETTEREK
  public HttpStatus getStatus() {
    return status;
  }



  public String getCode() {
    return code;
  }



  public List<BlacklistReasonResponse> getTiltasok() {
    return tiltasok;
  }


}


