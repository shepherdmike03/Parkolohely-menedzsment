/*____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/


package hu.shepherdmike.parkolo.dto;

import java.time.OffsetDateTime;
import java.util.List;


/*Szabad helyek*/
public record FreeSpotsResponse(

    OffsetDateTime kezdetIdo,
    OffsetDateTime vegIdo,
    Long jarmuId,
    long szabadHelyekSzama,
    List<SpotResponse> helyek
    ) 
{}


