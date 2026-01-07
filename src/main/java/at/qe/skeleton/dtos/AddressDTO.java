package at.qe.skeleton.dtos;

public record AddressDTO(
        Long id,
        String country,
        String city,
        String postalCode,
        String street,
        String number,
        String extra
) {}
