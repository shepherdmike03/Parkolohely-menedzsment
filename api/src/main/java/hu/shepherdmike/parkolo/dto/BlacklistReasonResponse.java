/*
  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/




package hu.shepherdmike.parkolo.dto;

import hu.shepherdmike.parkolo.entity.Tiltas;

import java.time.OffsetDateTime;


// valasz
public record BlacklistReasonResponse(
    Long tiltasId,
    String ok,
    OffsetDateTime tiltasKezdete,
    OffsetDateTime tiltasVege
    ) 
{

  public static BlacklistReasonResponse fromEntity(Tiltas tiltas) {
    return new BlacklistReasonResponse(
        tiltas.getId(),
        tiltas.getTiltasOk(),
        tiltas.getTiltasKezdete(),
        tiltas.getTiltasVege()
        );
  }

}


