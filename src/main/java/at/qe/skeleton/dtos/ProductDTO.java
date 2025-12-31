package at.qe.skeleton.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record ProductDTO(
    Long id,
    String name,
    String description,
    Double price,
    Integer stock,
    Double discount,
    Double avgScore,
    List<String> images,
    Boolean deleted,
    // Optional fields for admins/managers
    Long createdByName,
    LocalDateTime createdAt,
    Long updatedByName,
    LocalDateTime updatedAt
) {}

