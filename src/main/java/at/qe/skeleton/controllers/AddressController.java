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

/**
 * REST controller for managing user addresses.
 *
 * <p>
 * All endpoints operate on the currently authenticated user and allow
 * creating, retrieving, updating, and deleting addresses associated
 * with that user.
 * </p>
 */
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
     * Retrieves all addresses of the authenticated user.
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>200 OK - addresses successfully returned</li>
     * </ul>
     *
     * @return list of address DTOs belonging to the current user
     */

    @GetMapping
    public List<AddressDTO> getAddresses() {
        return userxService.getAddressesOfCurrentUser().stream()
                .map(addressMapper::toDTO)
                .toList();
    }

    /**
     * Creates a new address for the authenticated user.
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>201 Created - address successfully created</li>
     *   <li>400 Bad Request - validation failed</li>
     * </ul>
     *
     * @param dto address data to create
     * @return the newly created address
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressDTO addAddress(@Valid @RequestBody AddressCreateDTO dto) {
        Address address = addressCreateMapper.mapFrom(dto);
        return addressMapper.toDTO(userxService.addAddress(address));
    }

    /**
     * Partially updates an existing address of the authenticated user.
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>200 OK - address updated</li>
     *   <li>400 Bad Request - invalid update data</li>
     *   <li>404 Not Found - address not found</li>
     * </ul>
     *
     * @param id identifier of the address to update
     * @param dto fields to update
     * @return the updated address
     */
    @PatchMapping("/{id}")
    public AddressDTO updateAddress(@PathVariable Long id, @Valid @RequestBody AddressUpdateDTO dto) {
        return addressMapper.toDTO(userxService.updateAddress(id, dto));
    }

    /**
     * Deletes an address of the authenticated user.
     *
     * <p>Possible responses:</p>
     * <ul>
     *   <li>204 No Content - address deleted</li>
     *   <li>404 Not Found - address not found</li>
     * </ul>
     *
     * @param id identifier of the address to delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@PathVariable Long id) {
        userxService.removeAddress(id);
    }
}

