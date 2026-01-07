package at.qe.skeleton.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressCreateDTO(
        @NotBlank
        @Size(max = 100, message = "Country must not exceed 100 characters")
        String country,
        @NotBlank
        @Size(max = 100, message = "City must not exceed 100 characters")
        String city,
        @NotBlank
        @Size(max = 20, message = "PostalCode must not exceed 20 characters")
        String postalCode,
        @NotBlank
        @Size(max = 150, message = "Street must not exceed 150 characters")
        String street,
        @NotBlank
        @Size(max = 20, message = "Number must not exceed 20 characters")
        String number,
        // extra field is allowed to be blank
        @Size(max = 255, message = "Extra must not exceed 255 characters")
        String extra
) {}
