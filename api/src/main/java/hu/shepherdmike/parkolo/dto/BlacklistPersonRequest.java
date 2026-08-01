/*____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/


package hu.shepherdmike.parkolo.dto;


/*validacio*/
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;


// IDO
import java.time.OffsetDateTime;



public record BlacklistPersonRequest(

  @NotNull(message ="kotelezo a tulajdonosId")
  @Positive(message ="a tulajdonosId pozitiv szam kell legyen")
  Long tulajdonosId,

  @NotBlank(message ="a tiltas oka kotelezo")
  @Size(
  max =500,
  message= "a tiltas max 500 char lehet"
  )
  String ok,

  @Future(message = "a tiltas vege csak a jovoben lehet bruh")
  OffsetDateTime tiltasVege

  ) {



}




