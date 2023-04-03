package br.com.itstoony.attornatus.controller;

import br.com.itstoony.attornatus.dto.AddressRecord;
import br.com.itstoony.attornatus.dto.PersonDTO;
import br.com.itstoony.attornatus.dto.RegisteringPersonRecord;
import br.com.itstoony.attornatus.dto.UpdatingPersonRecord;
import br.com.itstoony.attornatus.model.Address;
import br.com.itstoony.attornatus.model.Person;
import br.com.itstoony.attornatus.service.AddressService;
import br.com.itstoony.attornatus.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Person", description = "API responsible for Person management")
public class PersonController {

    private final PersonService personService;

    private final ModelMapper modelMapper;

    private final AddressService addressService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a person")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Person registered successfully."),
            @ApiResponse(responseCode = "400", description = "Failed to register a person.")
    })
    public ResponseEntity<PersonDTO> register(@RequestBody @Valid RegisteringPersonRecord dto) {
        Address address = addressService.findFromDTO(dto);
        Person savedPerson = personService.register(dto, address);
        PersonDTO personDTO = modelMapper.map(savedPerson, PersonDTO.class);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(personDTO.getId()).toUri();

        return ResponseEntity.created(uri).body(personDTO);
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get details of a person by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person details successfully obtained."),
            @ApiResponse(responseCode = "400", description = "Failed to get person details.")
    })
    public ResponseEntity<PersonDTO> findById(@PathVariable(name = "id") Long id) {
        Person foundPerson = personService
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        return ResponseEntity.ok(modelMapper.map(foundPerson, PersonDTO.class));
    }

    @PutMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update a person.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person successfully updated."),
            @ApiResponse(responseCode = "400", description = "Failed to update person.")
    })
    public ResponseEntity<PersonDTO> update(@PathVariable(name = "id") Long id,
                                            @RequestBody UpdatingPersonRecord update) {
        Person person = personService
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));

        Person updatedPerson = personService.update(person, update);

        return ResponseEntity.ok(modelMapper.map(updatedPerson, PersonDTO.class));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Find people by params.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found people by parameters successfully obtained."),
            @ApiResponse(responseCode = "400", description = "Failed to find people by parameters.")
    })
    public ResponseEntity<Page<PersonDTO>> find(@PathParam("name") String name, Pageable pageable) {
        Page<Person> page = personService.find(name, pageable);
        List<PersonDTO> listDTO = page.stream().map(person -> modelMapper.map(person, PersonDTO.class)).toList();

        PageImpl<PersonDTO> pageDTO = new PageImpl<>(listDTO, pageable, page.getTotalElements());

        return ResponseEntity.ok(pageDTO);
    }

    @PostMapping(value = "{id}/address", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Add a new address to a person")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Address added successfully."),
            @ApiResponse(responseCode = "400", description = "Failed to add address.")
    })
    public ResponseEntity<PersonDTO> addAddress(@PathVariable(name = "id") Long id,
                                              @RequestBody @Valid AddressRecord dto) {

        Person person = personService
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));
        Address address = addressService.findFromDTO(dto);

        Person updatedPerson = personService.addAddress(person, address);

        return ResponseEntity.ok(modelMapper.map(updatedPerson, PersonDTO.class));
    }

    @GetMapping(value = "{id}/address", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Find all addresses from person.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found addresses by person successfully obtained."),
            @ApiResponse(responseCode = "400", description = "Failed to find addresses by person .")
    })
    public ResponseEntity<Page<Address>> listAllAddress(@PathVariable(name = "id") Long id,
                                                        Pageable pageable) {
        Person person = personService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));
        return ResponseEntity.ok(addressService.findAllAddress(person, pageable));
    }

    @PatchMapping(value = "{personID}/address/{addressID}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Set address as main.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address set as main successfully."),
            @ApiResponse(responseCode = "400", description = "Failed to set address as main.")
    })
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
