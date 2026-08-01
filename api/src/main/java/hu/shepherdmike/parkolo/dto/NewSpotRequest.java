//  ____  _                _                  _ __  __ _ _
// / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
// \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
//  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
// |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
//                  |_|

package hu.shepherdmike.parkolo.dto;


// jakarta library --- max, min, NOTNULL
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;



  // validalas
public record NewSpotRequest(
    @NotNull(message = "kotelezo szamot beirni!\n")
    @Min(value = 1, message = "legalabb egy helyet letre kell hozni")
    @Max(value = 500, message = "maximum 500 helynel tobbet ne lehessen letrehozni")
    Integer darab,

    @NotNull(message = "kotelezo a kategoriaId")
    Long kategoriaId
    ){

    }




