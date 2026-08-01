package hu.shepherdmike.parkolo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GenerateTestDataRequest(
    @NotNull
    @Min(1)
    @Max(1000)
    Integer szemelyek,

    @NotNull
    @Min(0)
    @Max(5000)
    Integer foglalasok,

    @NotNull
    @Min(0)
    @Max(100)
    Integer tiltolistaSzazalek,

    @NotNull
    @Min(0)
    @Max(500)
    Integer parkolohelyek
) {
}
