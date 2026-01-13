package at.qe.skeleton.services;

import at.qe.skeleton.dtos.AddressCreateDTO;
import at.qe.skeleton.dtos.AddressUpdateDTO;
import at.qe.skeleton.dtos.UserxRegistrationDTO;
import at.qe.skeleton.dtos.UserxUpdateDTO;
import at.qe.skeleton.exceptions.UsernameDuplicateException;
import at.qe.skeleton.mappers.AddressMapper;
import at.qe.skeleton.model.*;

import java.util.Collection;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.Set;

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
    private final AddressMapper addressMapper;

    @Autowired
    public UserxService(UserxRepository userRepository, PasswordEncoder passwordEncoder, AuthenticatedUserService authenticatedUserService, SubscriptionService subscriptionService, AddressMapper addressMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticatedUserService = authenticatedUserService;
        this.subscriptionService = subscriptionService;
        this.addressMapper = addressMapper;
    }
    
    /**
     * Returns a collection of all users.
     *
     * @return the userx collection
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<Userx> getAllUsers(
            int page,
            int limit,
            UserxRole role,
            Boolean deleted
    ) {
        Pageable pageable = PageRequest.of(page, limit);

        if (role != null && deleted != null) {
            return userRepository.findByRolesContainingAndDeleted(role, deleted, pageable);
        }
        if (role != null) {
            return userRepository.findByRolesContaining(role, pageable);
        }
        if (deleted != null) {
            return userRepository.findByDeleted(deleted, pageable);
        }

        return userRepository.findAll(pageable);
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
            // DEFAULT ROLE
            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                user.setRoles(Set.of(UserxRole.CUSTOMER));
            }
            // DEFAULT CHANNEL
            if (user.getChannels() == null || user.getChannels().isEmpty()) {
                user.setChannels(Set.of(NotificationType.EMAIL));
            }
            user.setCreateUser(authenticatedUserService.getAuthenticatedUser());
        } else {
            user.setUpdateUser(authenticatedUserService.getAuthenticatedUser());
        }
        return userRepository.save(user);
    }

    /**
     * Updates the user.
     *
     * @param id the id of the user to update
     * @param dto the updated user data
     * @return the updated user
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public Userx updateUser(Long id, UserxUpdateDTO dto) {
        Userx user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (dto.username() != null) user.setUsername(dto.username());
        if (dto.firstName() != null) user.setFirstName(dto.firstName());
        if (dto.lastName() != null) user.setLastName(dto.lastName());
        if (dto.email() != null) user.setEmail(dto.email());
        if (dto.phone() != null) user.setPhone(dto.phone());

        if (dto.password() != null) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        if (dto.roles() != null) user.setRoles(dto.roles());
        if (dto.channels() != null) user.setChannels(dto.channels());

        user.setUpdateUser(authenticatedUserService.getAuthenticatedUser());
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
            // only soft delete users
            userToDelete.setDeleted(true);
            userToDelete.setEnabled(false);
            userRepository.save(userToDelete);
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
    public List<Address> getAddressesOfCurrentUser() {
        return authenticatedUserService.requireAuthenticatedUser().getAddresses();
    }

    /**
     * Get address by id of authenticated user
     *
     * @param id the id of the address
     * @return address
     */
    public Address getAddressOfCurrentUserById(Long id) {
        return getAddressesOfCurrentUser().stream()
                .filter(address -> id.equals(address.getId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Address not found"
                ));
    }

    /**
     * add new address to authenticated user
     *
     * @param address the new address
     * @return address object
     */
    @Transactional
    public Address addAddress(Address address) {
        Userx user = authenticatedUserService.requireAuthenticatedUser();

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
    public Address updateAddress(Long addressId, AddressUpdateDTO dto) {
        Address address = getAddressOfCurrentUserById(addressId);
        addressMapper.apply(address, dto);
        return address;
    }

    /**
     * delete an address of the authenticated user
     *
     * @param addressId the id of the address to delete
     */
    @Transactional
    public void removeAddress(Long addressId) {
        Userx user = authenticatedUserService.requireAuthenticatedUser();
        user.getAddresses().removeIf(a -> addressId.equals(a.getId()));
    }

    @Transactional
    public Userx registerCustomer(UserxRegistrationDTO dto) {

        if (userRepository.existsByUsername(dto.username())) {
            throw new UsernameDuplicateException(
                    "Username " + dto.username() + " not available");
        }

        Userx user = new Userx();
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setEmail(dto.email());
        user.setPhone(dto.phone());

        // enforced defaults
        user.setRoles(Set.of(UserxRole.CUSTOMER));
        user.setChannels(Set.of(NotificationType.EMAIL));
        user.setEnabled(true);
        user.setDeleted(false);

        return userRepository.save(user);
    }

    public Userx getCurrentUser() {
        return authenticatedUserService.requireAuthenticatedUser();
    }

    @Transactional
    public Userx updateCurrentUser(UserxUpdateDTO dto) {

        Userx user = authenticatedUserService.requireAuthenticatedUser();

        if (dto.firstName() != null) user.setFirstName(dto.firstName());
        if (dto.lastName() != null) user.setLastName(dto.lastName());
        if (dto.email() != null) user.setEmail(dto.email());
        if (dto.phone() != null) user.setPhone(dto.phone());

        if (dto.password() != null) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        /*
        intentionally ignored:
        - roles
        - enabled
        - deleted
        - username
         */

        return userRepository.save(user);
    }

}
