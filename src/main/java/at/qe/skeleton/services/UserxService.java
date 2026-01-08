package at.qe.skeleton.services;

import at.qe.skeleton.dtos.AddressCreateDTO;
import at.qe.skeleton.dtos.AddressUpdateDTO;
import at.qe.skeleton.exceptions.UsernameDuplicateException;
import at.qe.skeleton.model.Address;
import at.qe.skeleton.model.Subscription;
import at.qe.skeleton.model.Userx;
import java.util.Collection;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import at.qe.skeleton.repositories.UserxRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * Service for accessing and manipulating user data.
 *
 * This class is part of the skeleton project provided for students of the
 * course "Software Architecture" offered by Innsbruck University.
 */
@Service
public class UserxService implements UserDetailsService {
 
    private final UserxRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedUserService authenticatedUserService;
    private final SubscriptionService subscriptionService;

    @Autowired
    public UserxService(UserxRepository userRepository, PasswordEncoder passwordEncoder, AuthenticatedUserService authenticatedUserService, SubscriptionService subscriptionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticatedUserService = authenticatedUserService;
        this.subscriptionService = subscriptionService;
    }
    
    /**
     * Returns a collection of all users.
     *
     * @return the userx collection
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public Collection<Userx> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Loads a single user identified by its id.
     *
     * @param id the id to search for
     * @return the user with the id
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public Optional<Userx> loadUser(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Saves the user. This method will also set {@link Userx#createDate} for new
     * entities or {@link Userx#updateDate} for updated entities. The user
     * requesting this operation will also be stored as {@link Userx#createDate}
     * or {@link Userx#updateUser} respectively.
     *
     * @param user the user to save
     * @return the updated user
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public Userx saveUser(Userx user) {
        if (user.isNew()) {
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new UsernameDuplicateException("Username " + user.getUsername() + " not available");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCreateUser(authenticatedUserService.getAuthenticatedUser());
        } else {
            user.setUpdateUser(authenticatedUserService.getAuthenticatedUser());
        }
        return userRepository.save(user);
    }

    /**
     * Deletes the user.
     * Deletes the user's subscriptions.
     *
     * @param user the user to delete
     */
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUser(Userx user) {
        userRepository.findById(user.getId()).ifPresent(userToDelete -> {
            Subscription[] subscriptions = subscriptionService.loadUserSubscriptions(userToDelete);
            for (Subscription s : subscriptions) {
                subscriptionService.deleteSubscription(s.getId());
            }
            userRepository.delete(userToDelete);
        });
    }

    public Userx getUserByUsername(String username) {
        return userRepository.findFirstByUsername(username).orElse(null);
    }


    /**
     * Loads a user by its username. Required for JWT authentication.
     *
     * @param username the username identifying the user whose data is required.
     * @return the user with the given username and their details.
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findFirstByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Get list of addresses of authenticated user
     * @return list of addresses of user
     */
    @PreAuthorize("isAuthenticated()")
    public List<Address> getAddressesOfCurrentUser() {
        return authenticatedUserService.requireAuthenticatedUser().getAddresses();
    }

    /**
     * add new address to authenticated user
     *
     * @param dto AddressCreateDTO with new address fields
     * @return address object
     */
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public Address addAddress(AddressCreateDTO dto) {
        Userx user = authenticatedUserService.requireAuthenticatedUser();

        Address address = new Address();
        address.setCountry(dto.country());
        address.setCity(dto.city());
        address.setPostalCode(dto.postalCode());
        address.setStreet(dto.street());
        address.setNumber(dto.number());
        address.setExtra(dto.extra());

        address.setUser(user);
        user.getAddresses().add(address);

        return address;
    }


    /**
     * update address of authenticated user
     *
     * @param addressId the id of the address to update
     * @param dto the AddressUpdateDTO with the updated address fields
     * @return the updated address
     */
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public Address updateAddress(Long addressId, AddressUpdateDTO dto) {
        Userx user = authenticatedUserService.requireAuthenticatedUser();

        Address address = user.getAddresses().stream()
                .filter(a -> addressId.equals(a.getId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Address not found"));

        if (dto.country() != null) address.setCountry(dto.country());
        if (dto.city() != null) address.setCity(dto.city());
        if (dto.postalCode() != null) address.setPostalCode(dto.postalCode());
        if (dto.street() != null) address.setStreet(dto.street());
        if (dto.number() != null) address.setNumber(dto.number());
        if (dto.extra() != null) address.setExtra(dto.extra());

        return address;
    }

    /**
     * delete an address of the authenticated user
     *
     * @param addressId the id of the address to delete
     */
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void removeAddress(Long addressId) {
        Userx user = authenticatedUserService.requireAuthenticatedUser();
        user.getAddresses().removeIf(a -> addressId.equals(a.getId()));
    }
}
