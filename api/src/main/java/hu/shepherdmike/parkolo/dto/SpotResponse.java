//  ____  _                _                  _ __  __ _ _
// / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
// \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
//  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
// |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
//                  |_|






package hu.shepherdmike.parkolo.dto;

import hu.shepherdmike.parkolo.entity.Parkolohely;


        /*Valasz a keresre*/
public record SpotResponse(
    Long parkolohelyId,
    String helyAzonosito,
    boolean aktiv,
    Long kategoriaId,
    String kategoriaNev
    ){

      public static SpotResponse fromEntity(Parkolohely parkolohely) {
        return new SpotResponse(
            parkolohely.getId(),
            parkolohely.getHelyAzonosito(),
            parkolohely.isAktiv(),
            parkolohely.getKategoria().getId(),
            parkolohely.getKategoria().getNev()
            );
      }

    }
