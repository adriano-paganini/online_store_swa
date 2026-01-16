package at.qe.skeleton.dtos;

import java.time.LocalDateTime;

public record PaymentResponseDTO(
        Boolean success,
        String transactionId,
        String message,
        LocalDateTime timestamp
) {}
