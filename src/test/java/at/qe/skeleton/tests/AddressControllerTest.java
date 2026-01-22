package at.qe.skeleton.tests;

import at.qe.skeleton.configs.JwtConfig;
import at.qe.skeleton.configs.JwtTokenProvider;
import at.qe.skeleton.configs.TokenAuthenticationFilter;
import at.qe.skeleton.controllers.AddressController;
import at.qe.skeleton.dtos.AddressCreateDTO;
import at.qe.skeleton.dtos.AddressDTO;
import at.qe.skeleton.dtos.AddressUpdateDTO;
import at.qe.skeleton.mappers.AddressCreateMapper;
import at.qe.skeleton.mappers.AddressMapper;
import at.qe.skeleton.model.Address;
import at.qe.skeleton.services.UserxService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

@WebMvcTest(AddressController.class)
@AutoConfigureMockMvc
class AddressControllerTest {

    private static final Long ADDRESS_ID = 1L;

    private static final String COUNTRY = "Austria";
    private static final String CITY = "Innsbruck";
    private static final String POSTAL_CODE = "6020";
    private static final String STREET = "Technikerstrasse";
    private static final String NUMBER = "1";
    private static final String EXTRA = "RR15";
    private static final String ENDPOINT = "/addresses";
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoSpyBean
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @MockitoBean
    private JwtConfig jwtConfig;

    @MockitoBean
    private UserxService userxService;

    @MockitoBean
    private AddressMapper addressMapper;

    @MockitoBean
    private AddressCreateMapper addressCreateMapper;

    private Address address;
    private AddressDTO addressDTO;

    @BeforeEach
    void setUp() throws Exception {
        address = new Address();
        address.setId(ADDRESS_ID);
        address.setCountry(COUNTRY);
        address.setCity(CITY);
        address.setPostalCode(POSTAL_CODE);
        address.setStreet(STREET);
        address.setNumber(NUMBER);
        address.setExtra(EXTRA);

        addressDTO = new AddressDTO(
                ADDRESS_ID,
                COUNTRY,
                CITY,
                POSTAL_CODE,
                STREET,
                NUMBER,
                EXTRA
        );

        Mockito.doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(tokenAuthenticationFilter).doFilterInternal(
                Mockito.any(HttpServletRequest.class),
                Mockito.any(HttpServletResponse.class),
                Mockito.any(FilterChain.class)
        );

        @SuppressWarnings("unchecked")
        Jws<Claims> mockJws = (Jws<Claims>) Mockito.mock(Jws.class);
        Claims mockClaims = Mockito.mock(Claims.class);
        Mockito.when(mockClaims.getSubject()).thenReturn("testuser");
        Mockito.when(mockJws.getPayload()).thenReturn(mockClaims);

        Mockito.when(jwtTokenProvider.validateTokenAndGetJws(Mockito.anyString()))
                .thenReturn(Optional.of(mockJws));
    }

    @Test
    @WithMockUser
    void getAddresses() throws Exception {
        Mockito.when(userxService.getAddressesOfCurrentUser())
                .thenReturn(List.of(address));
        Mockito.when(addressMapper.toDTO(address))
                .thenReturn(addressDTO);

        mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(ADDRESS_ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].city").value(CITY));
    }

    @Test
    @WithMockUser
    void addAddress() throws Exception {
        AddressCreateDTO createDTO = new AddressCreateDTO(
                COUNTRY, CITY, POSTAL_CODE, STREET, NUMBER, EXTRA
        );

        Mockito.when(addressCreateMapper.mapFrom(Mockito.any()))
                .thenReturn(address);
        Mockito.when(userxService.addAddress(Mockito.any()))
                .thenReturn(address);
        Mockito.when(addressMapper.toDTO(address))
                .thenReturn(addressDTO);

        mockMvc.perform(MockMvcRequestBuilders.post(ENDPOINT)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.city").value(CITY));
    }

    @Test
    @WithMockUser
    void addAddressBlankFieldFails() throws Exception {
        AddressCreateDTO dto = new AddressCreateDTO(
                COUNTRY,
                "",
                POSTAL_CODE,
                STREET,
                NUMBER,
                EXTRA
        );

        mockMvc.perform(MockMvcRequestBuilders.post(ENDPOINT)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateAddress() throws Exception {
        AddressUpdateDTO updateDTO = new AddressUpdateDTO(
                null, "Graz", null, null, null, null
        );

        String updatedCity = "Graz";

        Address updated = new Address();
        updated.setId(ADDRESS_ID);
        updated.setCity(updatedCity);

        AddressDTO updatedDTO = new AddressDTO(
                ADDRESS_ID, COUNTRY, updatedCity, POSTAL_CODE, STREET, NUMBER, EXTRA
        );

        Mockito.when(userxService.updateAddress(ADDRESS_ID, updateDTO))
                .thenReturn(updated);
        Mockito.when(addressMapper.toDTO(updated))
                .thenReturn(updatedDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch(ENDPOINT + "/{id}", ADDRESS_ID)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.city").value(updatedCity));
    }

    @Test
    @WithMockUser
    void deleteAddress() throws Exception {
        Mockito.doNothing()
                .when(userxService).removeAddress(ADDRESS_ID);

        mockMvc.perform(MockMvcRequestBuilders.delete(ENDPOINT + "/{id}", ADDRESS_ID)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void getAddressesUnauthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
