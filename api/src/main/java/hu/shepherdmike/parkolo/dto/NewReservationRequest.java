/*
  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/




package hu.shepherdmike.parkolo.dto;


// validacio anotaciok
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


// ido
import java.time.OffsetDateTime;




public record NewReservationRequest(

    @NotNull(message = "jarmuID kotelezo")
    @Positive(message = "A jarmuID positiv kell legyen!")
    Long jarmuId,



    @NotNull(message ="kotelezo a kezdoIdo")
    OffsetDateTime kezdetIdo,



    @NotNull(message ="kotelezo a VegIDO")
    OffsetDateTime vegIdo

    ) 
{}



