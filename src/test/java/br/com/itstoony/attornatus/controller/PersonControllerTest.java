package br.com.itstoony.attornatus.controller;

import br.com.itstoony.attornatus.dto.AddressRecord;
import br.com.itstoony.attornatus.dto.RegisteringPersonRecord;
import br.com.itstoony.attornatus.dto.UpdatingPersonRecord;
import br.com.itstoony.attornatus.model.Address;
import br.com.itstoony.attornatus.model.Person;
import br.com.itstoony.attornatus.service.AddressService;
import br.com.itstoony.attornatus.service.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Collections;
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

    @Test
    @DisplayName("Should update a person")
    public void updateTest() throws Exception {
        // scenery
        Long id = 1L;
        Person person = createPerson();
        person.setId(id);
        UpdatingPersonRecord update = new UpdatingPersonRecord("Sicrano", LocalDate.of(2000, 2, 7));
        Address address = createAddress();

        Person updatedPerson = new Person(id, update.name(), update.birthDay(), new HashSet<>());
        updatedPerson.getAddressSet().add(address);

        String json = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .writeValueAsString(update);

        BDDMockito.given(personService.findById(id)).willReturn(Optional.of(person));
        BDDMockito.given(personService.update(Mockito.any(Person.class),Mockito.any(UpdatingPersonRecord.class)))
                .willReturn(updatedPerson);

        // execution
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(PERSON_API.concat("/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // validation
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("name").value(update.name()))
                .andExpect(jsonPath("birthDay").value(update.birthDay().toString()))
                .andExpect(jsonPath("addressSet[0].id").value(address.getId()))
                .andExpect(jsonPath("addressSet[0].street").value(address.getStreet()))
                .andExpect(jsonPath("addressSet[0].zipcode").value(address.getZipcode()))
                .andExpect(jsonPath("addressSet[0].number").value(address.getNumber()))
                .andExpect(jsonPath("addressSet[0].city").value(address.getCity()));
    }

    @Test
    @DisplayName("Should return 404 when trying to update a person by an invalid ID")
    public void updateInvalidIDTest() throws Exception {
        // scenery
        Long id = 1L;
        String json = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .writeValueAsString(new UpdatingPersonRecord("name", LocalDate.now()));

        BDDMockito.given(personService.findById(id)).willReturn(Optional.empty());

        // execution
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(PERSON_API.concat("/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);


        // validation
        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should find and return a page of people by params")
    public void findTest() throws Exception {
        // scenery
        Person person = createPerson();

        BDDMockito.given( personService.find(Mockito.any(String.class), Mockito.any(Pageable.class)) )
                .willReturn(new PageImpl<>(Collections.singletonList(person), Pageable.ofSize(100), 1) );

        String queryString = String.format("?name=%s&page=0&size=10",
                person.getName());

        // execution
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(PERSON_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        // validation
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("content.[0].name").value(person.getName()))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(10))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }

    @Test
    @DisplayName("Should add an address to a persons set")
    public void addAddressTest() throws Exception {
        // scenery
        Long id = 1L;
        Person person = createPerson();
        Address address = createAddress();
        AddressRecord addressRecord = new AddressRecord(createAddress().getZipcode(), createAddress().getNumber());

        Person updatedPerson = createPerson();
        updatedPerson.setId(id);
        updatedPerson.getAddressSet().add(address);

        String json = new ObjectMapper().writeValueAsString(addressRecord);

        BDDMockito.given(personService.findById(id)).willReturn(Optional.of(person));
        BDDMockito.given(personService.addAddress(Mockito.any(Person.class), Mockito.any(Address.class)))
                .willReturn(updatedPerson);
        BDDMockito.given(addressService.findByZipcode(addressRecord.zipcode())).willReturn(address);

        // execution
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(PERSON_API.concat("/" + id + "/address"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // validation
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("name").value(person.getName()))
                .andExpect(jsonPath("birthDay").value(person.getBirthDay().toString()))
                .andExpect(jsonPath("addressSet[0].id").value(address.getId()))
                .andExpect(jsonPath("addressSet[0].street").value(address.getStreet()))
                .andExpect(jsonPath("addressSet[0].zipcode").value(addressRecord.zipcode()))
                .andExpect(jsonPath("addressSet[0].number").value(address.getNumber()))
                .andExpect(jsonPath("addressSet[0].city").value(address.getCity()));
    }

    @Test
    @DisplayName("Should return Bad Request when trying to add address with insufficient data")
    public void addAddressInvalidRecordTest() throws Exception {
        long id = 1L;
        AddressRecord addressRecord = new AddressRecord("", null);

        String json = new ObjectMapper().writeValueAsString(addressRecord);

        // execution
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(PERSON_API.concat("/" + id + "/address"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // validation
        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));
    }

    @Test
    @DisplayName("Should set an address as main")
    public void setAddressAsMainTest() throws Exception {
        // scenery
        Long id = 1L;
        Address address = createAddress();
        Person person = createPerson();

        address.setId(id);
        person.setId(id);
        address.setMain(true);
        person.getAddressSet().add(address);

        BDDMockito.given(personService.findById(id)).willReturn(Optional.of(createPerson()));
        BDDMockito.given(addressService.findById(id)).willReturn(Optional.of(createAddress()));
        BDDMockito.given(personService.setAddressAsMain(Mockito.any(Person.class), Mockito.any(Address.class)))
                .willReturn(person);
        // execution
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(PERSON_API.concat("/" + id + "/address/" + id))
                .accept(MediaType.APPLICATION_JSON);

        // validation
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("name").value(person.getName()))
                .andExpect(jsonPath("addressSet[0].id").value(address.getId()))
                .andExpect(jsonPath("addressSet[0].main").value(true))
                .andExpect(jsonPath("addressSet[0].street").value(address.getStreet()))
                .andExpect(jsonPath("addressSet[0].zipcode").value(address.getZipcode()))
                .andExpect(jsonPath("addressSet[0].number").value(address.getNumber()))
                .andExpect(jsonPath("addressSet[0].city").value(address.getCity()));
    }

    @Test
    @DisplayName("Should return 404 when trying to set an unavailable address as main  ")
    public void setUnavailableAddressAsMainTest() throws Exception {
        // scenery
        long id = 1L;

        BDDMockito.given(personService.findById(id)).willReturn(Optional.of(createPerson()));
        // execution
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .patch(PERSON_API.concat("/" + id + "/address/" + id))
                .accept(MediaType.APPLICATION_JSON);

        // validation
        mvc
                .perform(request)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Address not found"));

    }

    @Test
    @DisplayName("Should return a page with all addresses from a person by it's id")
    public void findAllAddressTest() throws Exception {
        // scenery
        Long id = 1L;
        Person person = createPerson();
        Address address = createAddress();

        Person updatedPerson = createPerson();
        updatedPerson.setId(id);
        updatedPerson.getAddressSet().add(address);

        BDDMockito.given(personService.findById(id)).willReturn(Optional.of(person));
        BDDMockito.given(personService.findAllAddress(Mockito.any(Person.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<>(Collections.singletonList(address), Pageable.ofSize(100), 1));
        // execution
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(PERSON_API.concat("/" + id + "/address?page=0&size=10"))
                .accept(MediaType.APPLICATION_JSON);

        // implementation
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("content[0].id").value(address.getId()))
                .andExpect(jsonPath("content[0].street").value(address.getStreet()))
                .andExpect(jsonPath("content[0].zipcode").value(address.getZipcode()))
                .andExpect(jsonPath("content[0].number").value(address.getNumber()))
                .andExpect(jsonPath("content[0].city").value(address.getCity()))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));

    }

    private static Address createAddress() {
        return Address.builder()
                .id(1L)
                .street("Rua Exemplo")
                .zipcode("12345678")
                .number(123)
                .city("São Paulo")
                .main(false)
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

    private static RegisteringPersonRecord createRegisteringPersonDTO() {
        return new RegisteringPersonRecord("Fulano", LocalDate.of(1998, 11, 25), "22222222" );
    }
}
