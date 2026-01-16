package at.qe.skeleton.mappers;

import at.qe.skeleton.dtos.AddressCreateDTO;
import at.qe.skeleton.model.Address;
import org.springframework.stereotype.Service;

@Service
public class AddressCreateMapper {
    public Address mapFrom(AddressCreateDTO dto) {
        if  (dto == null) {
            return null;
        }

        Address address = new Address();
        address.setCity(dto.city());
        address.setCountry(dto.country());
        address.setStreet(dto.street());
        address.setPostalCode(dto.postalCode());
        address.setNumber(dto.number());
        address.setExtra(dto.extra());
        return address;
    }
}
