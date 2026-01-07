package at.qe.skeleton.controllers;


import at.qe.skeleton.dtos.AddressCreateDTO;
import at.qe.skeleton.dtos.AddressDTO;
import at.qe.skeleton.dtos.AddressUpdateDTO;
import at.qe.skeleton.mappers.AddressMapper;
import at.qe.skeleton.services.UserxService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    private final UserxService userxService;
    private final AddressMapper addressMapper;

    public AddressController(UserxService userxService, AddressMapper addressMapper) {
        this.userxService = userxService;
        this.addressMapper = addressMapper;
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
    public AddressDTO addAddress(@Valid @RequestBody AddressCreateDTO dto) {
        return addressMapper.toDTO(userxService.addAddress(dto));
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
    public void deleteAddress(@PathVariable Long id) {
        userxService.removeAddress(id);
    }
}

