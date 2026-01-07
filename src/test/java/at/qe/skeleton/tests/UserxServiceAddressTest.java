package at.qe.skeleton.tests;

import at.qe.skeleton.dtos.AddressCreateDTO;
import at.qe.skeleton.dtos.AddressUpdateDTO;
import at.qe.skeleton.model.Address;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.repositories.UserxRepository;
import at.qe.skeleton.services.AuthenticatedUserService;
import at.qe.skeleton.services.UserxService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class UserxServiceAddressTest {
    private static final Long USER_ID = 1L;
    private static final String USERNAME = "testuser";

    private static final Long ADDRESS_ID = 1L;
    private static final Long OTHER_ADDRESS_ID = 2L;

    private static final String COUNTRY = "Austria";
    private static final String CITY_INNSBRUCK = "Innsbruck";
    private static final String CITY_GRAZ = "Graz";
    private static final String POSTAL_CODE = "6020";
    private static final String STREET = "Technikerstrasse";
    private static final String NUMBER = "1";
    private static final String EXTRA = "RR15";

    @Autowired
    private UserxService userxService;

    @MockitoBean
    private AuthenticatedUserService authenticatedUserService;

    @MockitoBean
    private UserxRepository userxRepository;

    private Userx testUser;

    @BeforeEach
    void setUp() {
        testUser = new Userx();
        testUser.setId(USER_ID);
        testUser.setUsername(USERNAME);
        testUser.setAddresses(new ArrayList<>());

        Mockito.when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(testUser);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void addAddressSuccess() {
        AddressCreateDTO dto = new AddressCreateDTO(
                COUNTRY,
                CITY_INNSBRUCK,
                POSTAL_CODE,
                STREET,
                NUMBER,
                EXTRA
        );

        Address result = userxService.addAddress(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(COUNTRY, result.getCountry());
        Assertions.assertEquals(CITY_INNSBRUCK, result.getCity());
        Assertions.assertEquals(testUser, result.getUser());
        Assertions.assertEquals(1, testUser.getAddresses().size());
    }

    @Test
    void addAddressUnauthenticatedFails() {
        Mockito.when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(null);

        AddressCreateDTO dto = new AddressCreateDTO(
                COUNTRY, CITY_INNSBRUCK, POSTAL_CODE, STREET, NUMBER, EXTRA
        );

        Assertions.assertThrows(
                AuthenticationCredentialsNotFoundException.class,
                () -> userxService.addAddress(dto)
        );
    }

    @Test
    @WithMockUser(username = USERNAME)
    void addAddressAuthenticatedButUserMissingInDbFails() {
        Mockito.when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(null);

        AddressCreateDTO dto = new AddressCreateDTO(
                COUNTRY, CITY_INNSBRUCK, POSTAL_CODE, STREET, NUMBER, EXTRA
        );

        ResponseStatusException ex =
                Assertions.assertThrows(ResponseStatusException.class,
                        () -> userxService.addAddress(dto));

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    @WithMockUser(username = USERNAME)
    void getAddressesOfCurrentUser() {
        Address address = new Address();
        address.setId(ADDRESS_ID);
        address.setCity(CITY_INNSBRUCK);
        address.setUser(testUser);

        testUser.getAddresses().add(address);

        List<Address> result = userxService.getAddressesOfCurrentUser();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(CITY_INNSBRUCK, result.get(0).getCity());
    }
    
    @Test
    @WithMockUser(username = USERNAME)
    void updateAddressPartialUpdateSuccess() {
        Address address = new Address();
        address.setId(ADDRESS_ID);
        address.setCountry(COUNTRY);
        address.setCity(CITY_INNSBRUCK);
        address.setUser(testUser);

        testUser.getAddresses().add(address);

        AddressUpdateDTO dto = new AddressUpdateDTO(
                null,
                CITY_GRAZ,
                null,
                null,
                null,
                null
        );

        Address updated = userxService.updateAddress(ADDRESS_ID, dto);

        Assertions.assertEquals(CITY_GRAZ, updated.getCity());
        Assertions.assertEquals(COUNTRY, updated.getCountry()); // unchanged
    }

    @Test
    @WithMockUser(username = USERNAME)
    void updateAddressNotOwnedByUserFails() {
        testUser.setAddresses(new ArrayList<>());

        AddressUpdateDTO dto = new AddressUpdateDTO(
                COUNTRY, null, null, null, null, null
        );

        ResponseStatusException ex =
                Assertions.assertThrows(ResponseStatusException.class, () ->
                        userxService.updateAddress(OTHER_ADDRESS_ID, dto));

        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());

    }

    @Test
    @WithMockUser(username = USERNAME)
    void removeAddressSuccess() {
        Address address = new Address();
        address.setId(ADDRESS_ID);
        address.setUser(testUser);

        testUser.getAddresses().add(address);
        Assertions.assertEquals(1, testUser.getAddresses().size());

        userxService.removeAddress(ADDRESS_ID);

        Assertions.assertEquals(0, testUser.getAddresses().size());
    }
}
