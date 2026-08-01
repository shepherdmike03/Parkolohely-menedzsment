package hu.shepherdmike.parkolo.dto;

import hu.shepherdmike.parkolo.entity.Tulajdonos;

public record PersonResponse(
    Long tulajdonosId,
    String keresztnev,
    String csaladnev,
    String teljesNev,
    Long specialisStatusId
) {
    public static PersonResponse fromEntity(Tulajdonos tulajdonos) {
        return new PersonResponse(
            tulajdonos.getId(),
            tulajdonos.getKeresztnev(),
            tulajdonos.getCsaladnev(),
            tulajdonos.getTeljesNev(),
            tulajdonos.getSpecialisStatusId()
        );
    }
}
