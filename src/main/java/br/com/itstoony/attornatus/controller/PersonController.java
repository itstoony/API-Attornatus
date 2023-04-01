package br.com.itstoony.attornatus.controller;

import br.com.itstoony.attornatus.dto.PersonDTO;
import br.com.itstoony.attornatus.dto.RegisteringPersonRecord;
import br.com.itstoony.attornatus.model.Address;
import br.com.itstoony.attornatus.model.Person;
import br.com.itstoony.attornatus.service.AddressService;
import br.com.itstoony.attornatus.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/person")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    private final ModelMapper modelMapper;

    private final AddressService addressService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonDTO> register(@RequestBody @Valid RegisteringPersonRecord dto) {
        Address address = addressService.findByZipcode(dto.zipcode());
        Person savedPerson = personService.register(dto, address);
        PersonDTO personDTO = modelMapper.map(savedPerson, PersonDTO.class);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(personDTO.getId()).toUri();

        return ResponseEntity.created(uri).body(personDTO);
    }

}
