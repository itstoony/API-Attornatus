package br.com.itstoony.attornatus.controller;

import br.com.itstoony.attornatus.dto.AddressRecord;
import br.com.itstoony.attornatus.dto.PersonDTO;
import br.com.itstoony.attornatus.dto.RegisteringPersonRecord;
import br.com.itstoony.attornatus.dto.UpdatingPersonRecord;
import br.com.itstoony.attornatus.model.Address;
import br.com.itstoony.attornatus.model.Person;
import br.com.itstoony.attornatus.service.AddressService;
import br.com.itstoony.attornatus.service.PersonService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

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

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonDTO> findById(@PathVariable(name = "id") Long id) {
        Person foundPerson = personService
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        return ResponseEntity.ok(modelMapper.map(foundPerson, PersonDTO.class));
    }

    @PutMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PersonDTO> update(@PathVariable(name = "id") Long id,
                                            @RequestBody UpdatingPersonRecord update) {
        Person person = personService
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        Person updatedPerson = personService.update(person, update);

        return ResponseEntity.ok(modelMapper.map(updatedPerson, PersonDTO.class));
    }

    @GetMapping
    public ResponseEntity<Page<PersonDTO>> find(@PathParam("name") String name, Pageable pageable) {
        Page<Person> page = personService.find(name, pageable);
        List<PersonDTO> listDTO = page.stream().map(person -> modelMapper.map(person, PersonDTO.class)).toList();

        PageImpl<PersonDTO> pageDTO = new PageImpl<>(listDTO, pageable, page.getTotalElements());

        return ResponseEntity.ok(pageDTO);
    }

    @PostMapping("{id}/address")
    public ResponseEntity<PersonDTO> addAddress(@PathVariable(name = "id") Long id,
                                              @RequestBody @Valid AddressRecord dto) {

        Person person = personService
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));
        Address address = addressService.findByZipcode(dto.zipcode());

        Person updatedPerson = personService.addAddress(person, address);

        return ResponseEntity.ok(modelMapper.map(updatedPerson, PersonDTO.class));
    }

    @GetMapping("{id}/address")
    public ResponseEntity<Page<Address>> listAllAddress(@PathVariable(name = "id") Long id,
                                                        Pageable pageable) {
        Person person = personService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));
        return ResponseEntity.ok(personService.findAllAddress(person, pageable));
    }

    @PatchMapping("{personID}/address/{addressID}")
    public ResponseEntity<PersonDTO> setAddressAsMain(@PathVariable(name = "personID") Long personID,
                                                      @PathVariable(name = "addressID") Long addressID) {
        Person person = personService.findById(personID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        Address address = addressService.findById(addressID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        Person updatedPerson = personService.setAddressAsMain(person, address);

        return ResponseEntity.ok(modelMapper.map(updatedPerson, PersonDTO.class));
    }

}
