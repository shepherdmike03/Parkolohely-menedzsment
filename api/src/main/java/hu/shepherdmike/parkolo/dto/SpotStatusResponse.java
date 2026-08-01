/*
  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/


package hu.shepherdmike.parkolo.dto;

import java.time.OffsetDateTime;



/*Statusz valasz*/
public record SpotStatusResponse(
  
  OffsetDateTime kezdetIdo,
  OffsetDateTime vegIdo,
  long osszesAktivHely,
  long foglaltHely,
  long szabadHely
) 
{}



