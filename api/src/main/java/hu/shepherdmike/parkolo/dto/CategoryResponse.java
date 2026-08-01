/*
  ____  _                _                  _ __  __ _ _                     _    ___ 
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____     _       / \  |_ _|
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \  _| |_    / _ \  | | 
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/ |_   _|  / ___ \ | | 
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|   |_|   /_/   \_\___|
                  |_|                                                                 
*/



package hu.shepherdmike.parkolo.dto;

import hu.shepherdmike.parkolo.entity.Kategoria;


public record CategoryResponse(
    Long kategoriaId,
    String kategoriaNev,
    Integer meretSorrend
) {

  public static CategoryResponse fromEntity(
      Kategoria kategoria
  ) {
    return new CategoryResponse(
        kategoria.getId(),
        kategoria.getNev(),
        kategoria.getMeretSorrend()
    );
  }
}
