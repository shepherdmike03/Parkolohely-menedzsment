/*
  ____  _                _                  _ __  __ _ _                     _    ___ 
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____     _       / \  |_ _|
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \  _| |_    / _ \  | | 
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/ |_   _|  / ___ \ | | 
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|   |_|   /_/   \_\___|
                  |_|                                                                 
*/

package hu.shepherdmike.parkolo.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;


public record NewCustomerVehicleRequest(

    @NotBlank(message = "a keresztnev kotelezo")
    @Size(max = 100, message = "a keresztnev maximum 100 karakter")
    String keresztnev,

    @NotBlank(message = "a csaladnev kotelezo")
    @Size(max = 100, message = "a csaladnev maximum 100 karakter")
    String csaladnev,

    @Positive(message = "a specialisStatusId pozitiv szam kell legyen")
    Long specialisStatusId,

    @NotNull(message = "a kategoriaId kotelezo")
    @Positive(message = "a kategoriaId pozitiv szam kell legyen")
    Long kategoriaId,

    @NotBlank(message = "a rendszam kotelezo")
    @Size(max = 20, message = "a rendszam maximum 20 karakter")
    String rendszam

) {
}
