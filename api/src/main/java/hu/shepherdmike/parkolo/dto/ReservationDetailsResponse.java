/*
  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/




package hu.shepherdmike.parkolo.dto;


import hu.shepherdmike.parkolo.entity.Foglalas;

// ido
import java.time.OffsetDateTime;
import java.time.ZoneOffset;



public record ReservationDetailsResponse(
    Long foglalasId,

    Long jarmuId,
    String rendszam,

    Long tulajdonosId,
    String tulajdonosNev,

    Long parkolohelyId,
    String helyAzonosito,

    String jarmuKategoria,
    String parkolohelyKategoria,

    OffsetDateTime kezdetIdo,
    OffsetDateTime vegIdo,

    OffsetDateTime erkezesIdo,
    OffsetDateTime elhagyasIdo,

    OffsetDateTime letrehozva,
    String allapot
    ) 
{
        /*valasz*/
      public static ReservationDetailsResponse fromEntity(
          Foglalas foglalas
          ) 
      {
        return new ReservationDetailsResponse(
            foglalas.getId(),
                                                                        /*Reszletek kiirasa*/
            foglalas.getJarmu().getId(),
            foglalas.getJarmu().getRendszam(),

            foglalas.getJarmu().getTulajdonos().getId(),
            foglalas.getJarmu().getTulajdonos().getTeljesNev(),

            foglalas.getParkolohely().getId(),
            foglalas.getParkolohely().getHelyAzonosito(),

            foglalas.getJarmu().getKategoria().getNev(),
            foglalas.getParkolohely().getKategoria().getNev(),

            foglalas.getKezdetIdo(),
            foglalas.getVegIdo(),

            foglalas.getErkezesIdo(),
            foglalas.getElhagyasIdo(),

            foglalas.getLetrehozva(),
            calculateStatus(foglalas)
            );
      }



        /*Status calculacio*/
      private static String calculateStatus(Foglalas foglalas) {
      
        OffsetDateTime most =OffsetDateTime.now(ZoneOffset.UTC);

        if (foglalas.getElhagyasIdo() != null) {
          return "LEZART";
        }

        if (foglalas.getErkezesIdo() != null) {
          return "PARKOL";
        }

        if (most.isBefore(foglalas.getKezdetIdo())) {
          return "TERVEZETT";
        }

        if (most.isBefore(foglalas.getVegIdo())) {
          return "ERKEZESRE_VAR";
        }

        return "LEJART";
      }

}



