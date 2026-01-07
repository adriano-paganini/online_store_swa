package at.qe.skeleton.mappers;

import at.qe.skeleton.dtos.AddressDTO;
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
}
