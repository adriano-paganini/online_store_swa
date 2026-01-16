package at.qe.skeleton.mappers;

import at.qe.skeleton.dtos.AddressDTO;
import at.qe.skeleton.dtos.AddressUpdateDTO;
import at.qe.skeleton.model.Address;
import org.springframework.stereotype.Service;

@Service
public class AddressMapper {
    public AddressDTO toDTO(Address address) {
        if  (address == null) {
            return null;
        }

        return new AddressDTO(
                address.getId(),
                address.getCountry(),
                address.getCity(),
                address.getPostalCode(),
                address.getStreet(),
                address.getNumber(),
                address.getExtra()
        );
    }

    public void apply(Address address, AddressUpdateDTO dto) {
        if (dto.country() != null) address.setCountry(dto.country());
        if (dto.city() != null) address.setCity(dto.city());
        if (dto.postalCode() != null) address.setPostalCode(dto.postalCode());
        if (dto.street() != null) address.setStreet(dto.street());
        if (dto.number() != null) address.setNumber(dto.number());
        if (dto.extra() != null) address.setExtra(dto.extra());
    }
}
