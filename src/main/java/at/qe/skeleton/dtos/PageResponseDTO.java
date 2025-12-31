package at.qe.skeleton.dtos;

import java.util.List;

public record PageResponseDTO<T>(
    List<T> data,
    Integer page,
    Integer limit,
    Long totalElements,
    Integer totalPages
) {}

