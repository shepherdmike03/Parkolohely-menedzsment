package hu.shepherdmike.parkolo.dto;

import hu.shepherdmike.parkolo.entity.Tiltas;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public record BlacklistEntryResponse(
    Long tiltasId,
    Long tulajdonosId,
    String tulajdonosNev,
    String ok,
    OffsetDateTime tiltasKezdete,
    OffsetDateTime tiltasVege,
    boolean aktiv
) {
    public static BlacklistEntryResponse fromEntity(Tiltas tiltas) {
        OffsetDateTime most = OffsetDateTime.now(ZoneOffset.UTC);
        boolean jelenlegAktiv = tiltas.isAktiv()
            && (tiltas.getTiltasVege() == null || tiltas.getTiltasVege().isAfter(most));

        return new BlacklistEntryResponse(
            tiltas.getId(),
            tiltas.getTulajdonos().getId(),
            tiltas.getTulajdonos().getTeljesNev(),
            tiltas.getTiltasOk(),
            tiltas.getTiltasKezdete(),
            tiltas.getTiltasVege(),
            jelenlegAktiv
        );
    }
}
