package at.qe.skeleton.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentRequestDTO(
        @NotNull
        @Positive
        Double amount,
        @NotBlank
        String paymentMethod,
        String cardNumber,
        String cardHolderName,
        String expiryDate,
        String cvv
) {}
