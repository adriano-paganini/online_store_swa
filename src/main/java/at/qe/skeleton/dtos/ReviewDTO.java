package at.qe.skeleton.dtos;

import java.time.LocalDateTime;

public record ReviewDTO(
    Long productId,
    String authorName,
    Integer score,
    String content,
    LocalDateTime timestamp
) {}

