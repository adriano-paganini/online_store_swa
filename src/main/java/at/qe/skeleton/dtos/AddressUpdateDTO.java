package at.qe.skeleton.dtos;

import jakarta.validation.constraints.Size;

public record AddressUpdateDTO(
        @Size(max = 100, message = "Country must not exceed 100 characters")
        String country,
        @Size(max = 100, message = "City must not exceed 100 characters")
        String city,
        @Size(max = 20, message = "PostalCode must not exceed 20 characters")
        String postalCode,
        @Size(max = 150, message = "Street must not exceed 150 characters")
        String street,
        @Size(max = 20, message = "Number must not exceed 20 characters")
        String number,
        @Size(max = 255, message = "Extra must not exceed 255 characters")
        String extra
) {}
