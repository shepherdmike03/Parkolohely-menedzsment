package hu.shepherdmike.parkolo.dto;

import hu.shepherdmike.parkolo.entity.Jarmu;

public record VehicleResponse(
    Long jarmuId,
    String rendszam,
    Long tulajdonosId,
    String tulajdonosNev,
    Long kategoriaId,
    String kategoriaNev
) {
    public static VehicleResponse fromEntity(Jarmu jarmu) {
        return new VehicleResponse(
            jarmu.getId(),
            jarmu.getRendszam(),
            jarmu.getTulajdonos().getId(),
            jarmu.getTulajdonos().getTeljesNev(),
            jarmu.getKategoria().getId(),
            jarmu.getKategoria().getNev()
        );
    }
}
