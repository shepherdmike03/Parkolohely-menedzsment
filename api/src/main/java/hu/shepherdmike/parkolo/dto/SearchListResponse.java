package hu.shepherdmike.parkolo.dto;

import java.util.List;

public record SearchListResponse<T>(
    long osszesen,
    List<T> elemek
) {
}
