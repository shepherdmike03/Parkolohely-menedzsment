package hu.shepherdmike.parkolo.dto;

public record GenerateTestDataResponse(
    int letrehozottKategoriak,
    int letrehozottSzemelyek,
    int letrehozottJarmuvek,
    int letrehozottParkolohelyek,
    int letrehozottTiltasok,
    int letrehozottFoglalasok
) {
}
