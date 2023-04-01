package br.com.itstoony.attornatus.controller;

import br.com.itstoony.attornatus.dto.PersonDTO;
import br.com.itstoony.attornatus.dto.RegisteringPersonRecord;
import br.com.itstoony.attornatus.model.Address;
import br.com.itstoony.attornatus.model.Person;
import br.com.itstoony.attornatus.service.AddressService;
import br.com.itstoony.attornatus.service.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WebMvcTest
public class PersonControllerTest {

    static String PERSON_API = "/person";

    @Autowired
    MockMvc mvc;

    @MockBean
    PersonService personService;

    @MockBean
    AddressService addressService;


    @Test
    @DisplayName("Should register a person")
    public void registerTest() throws Exception {
        // scenery
        RegisteringPersonRecord dto = createRegisteringPersonDTO();
        Person savedPerson = createPerson();
        savedPerson.setId(1L);

        Address address = createAddress();

        savedPerson.getAddressSet().add(address);

        String json = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .writeValueAsString(dto);

        BDDMockito.given(addressService.findByZipcode(dto.zipcode())).willReturn(address);
        BDDMockito.given(personService.register(Mockito.any(RegisteringPersonRecord.class), Mockito.any(Address.class))).willReturn(savedPerson);

        // execution
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(PERSON_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // validation
        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(savedPerson.getId()))
                .andExpect(jsonPath("name").value(dto.name()))
                .andExpect(jsonPath("birthDay").value(dto.birthDay().toString()))
                .andExpect(jsonPath("addressSet[0].id").value(address.getId()))
                .andExpect(jsonPath("addressSet[0].street").value(address.getStreet()))
                .andExpect(jsonPath("addressSet[0].zipcode").value(address.getZipcode()))
                .andExpect(jsonPath("addressSet[0].number").value(address.getNumber()))
                .andExpect(jsonPath("addressSet[0].city").value(address.getCity()));

    }

    @Test
    @DisplayName("Should return 400 Bad Request when trying to register a person without sufficient data")
    public void registerInvalidPersonTest() throws Exception {
        // scenery
        RegisteringPersonRecord dto = new RegisteringPersonRecord("", null, "");

        String json = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .writeValueAsString(dto);

        // execution
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(PERSON_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // validation
        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(4)));
    }

    @Test
    @DisplayName("Should find a person by it's ID")
    public void findByIDTest() throws Exception {
        // scenery
        Person person = createPerson();
        Address address = createAddress();
        person.getAddressSet().add(address);
        Long id = 1L;
        person.setId(id);

        BDDMockito.given( personService.findById(Mockito.anyLong()) ).willReturn(Optional.of(person));

        // execution
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(PERSON_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        // validation
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("name").value(person.getName()))
                .andExpect(jsonPath("birthDay").value(person.getBirthDay().toString()))
                .andExpect(jsonPath("addressSet[0].id").value(address.getId()))
                .andExpect(jsonPath("addressSet[0].street").value(address.getStreet()))
                .andExpect(jsonPath("addressSet[0].zipcode").value(address.getZipcode()))
                .andExpect(jsonPath("addressSet[0].number").value(address.getNumber()))
                .andExpect(jsonPath("addressSet[0].city").value(address.getCity()));

    }

    @Test
    @DisplayName("Should return 404 not found when trying to find a person by an invalid ID")
    public void findByInvalidIDTest() throws Exception {
        // scenery
        Long id = 1L;

        BDDMockito.given(personService.findById(id)).willReturn(Optional.empty());

        // execution
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(PERSON_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);


        // validation
        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }


    private static Address createAddress() {
        return Address.builder()
                .id(1L)
                .street("Rua Exemplo")
                .zipcode("12345678")
                .number(123)
                .city("São Paulo")
                .build();
    }

    private static Person createPerson() {
        return Person.builder()
                .id(1L)
                .name("Fulano")
                .birthDay(LocalDate.of(1998, 11, 25))
                .addressSet(new HashSet<>())
                .build();
    }

    private static PersonDTO createPersonDTO() {
        return PersonDTO.builder()
                .name("Fulano")
                .birthDay(LocalDate.of(1998, 11, 25 ))
                .addressSet(new HashSet<>())
                .build();
    }

    private static RegisteringPersonRecord createRegisteringPersonDTO() {
        return new RegisteringPersonRecord("Fulano", LocalDate.of(1998, 11, 25), "22222222" );
    }
}
