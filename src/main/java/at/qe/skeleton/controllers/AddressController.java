package at.qe.skeleton.controllers;


import at.qe.skeleton.dtos.AddressCreateDTO;
import at.qe.skeleton.dtos.AddressDTO;
import at.qe.skeleton.dtos.AddressUpdateDTO;
import at.qe.skeleton.mappers.AddressCreateMapper;
import at.qe.skeleton.mappers.AddressMapper;
import at.qe.skeleton.model.Address;
import at.qe.skeleton.services.UserxService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    private final UserxService userxService;
    private final AddressMapper addressMapper;
    private final AddressCreateMapper addressCreateMapper;

    public AddressController(UserxService userxService, AddressMapper addressMapper, AddressCreateMapper addressCreateMapper) {
        this.userxService = userxService;
        this.addressMapper = addressMapper;
        this.addressCreateMapper = addressCreateMapper;
    }

    /**
     * Get all addresses of the authenticated user
     */
    @GetMapping
    public List<AddressDTO> getAddresses() {
        return userxService.getAddressesOfCurrentUser().stream()
                .map(addressMapper::toDTO)
                .toList();
    }

    /**
     * Add new address
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressDTO addAddress(@Valid @RequestBody AddressCreateDTO dto) {
        Address address = addressCreateMapper.mapFrom(dto);
        return addressMapper.toDTO(userxService.addAddress(address));
    }

    /**
     * Update address
     */
    @PatchMapping("/{id}")
    public AddressDTO updateAddress(@PathVariable Long id, @Valid @RequestBody AddressUpdateDTO dto) {
        return addressMapper.toDTO(userxService.updateAddress(id, dto));
    }

    /**
     * Delete address
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@PathVariable Long id) {
        userxService.removeAddress(id);
    }
}

