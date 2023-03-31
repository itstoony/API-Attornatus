package br.com.itstoony.attornatus.controller;

import br.com.itstoony.attornatus.dto.PersonDTO;
import br.com.itstoony.attornatus.model.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.HashSet;

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

    @Test
    @DisplayName("Should register a person")
    public void registerTest() throws Exception {
        // scenery
        PersonDTO personDTO = createPersonDTO();

        Person savedPerson = createPerson();

        String json = new ObjectMapper().writeValueAsString(personDTO);

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
                .andExpect(jsonPath("name").value(personDTO.getName()))
                .andExpect(jsonPath("birthDay").value(personDTO.getBirthDay()))
                .andExpect(jsonPath("addressSet").value(personDTO.getAddressSet()));

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
}
