package br.com.itstoony.attornatus.service;

import br.com.itstoony.attornatus.dto.RegisteringPersonRecord;
import br.com.itstoony.attornatus.dto.UpdatingPersonRecord;
import br.com.itstoony.attornatus.exception.BusinessException;
import br.com.itstoony.attornatus.model.Address;
import br.com.itstoony.attornatus.model.Person;
import br.com.itstoony.attornatus.repository.AddressRepository;
import br.com.itstoony.attornatus.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class PersonServiceTest {

    PersonService personService;

    @MockBean
    PersonRepository personRepository;

    @MockBean
    AddressRepository addressRepository;

    @BeforeEach
    public void setUp() {
        this.personService = new PersonService(personRepository, addressRepository);
    }

    @Test
    @DisplayName("Should register a person on database")
    public void registerTest() {
        // scenery
        RegisteringPersonRecord dto = createRegisteringPersonDTO();
        Address address = createAddress();
        address.setId(1L);

        Person savedPerson = createPerson();
        savedPerson.setId(1L);
        savedPerson.getAddressSet().add(address);

        when(addressRepository.save(any(Address.class)))
                .thenReturn(address);
        when(personRepository.save(any(Person.class)))
                .thenReturn(savedPerson);

        // execution
        Person saved = personService.register(dto, address);

        // implementation
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo(dto.name());
        assertThat(saved.getCpf()).isEqualTo(dto.cpf());
        assertThat(saved.getBirthDay()).isEqualTo(dto.birthDay());
        assertThat(saved.getAddressSet().contains(address)).isTrue();

        assertThat(saved.getAddressSet()
                .stream()
                .findFirst()
                .get().getZipcode())
                .isEqualTo(dto.zipcode());

        assertThat(saved.getAddressSet().
                stream()
                .findFirst()
                .get()
                .getNumber())
                .isEqualTo(dto.number());

        assertThat(saved.getAddressSet().
                stream()
                .findFirst()
                .get()
                .getMain())
                .isTrue();
    }

    @Test
    @DisplayName("Should throw a BusinessException when trying to save a person with an already saved CPF")
    public void saveAlreadySavedPersonTest() {
        // scenery
        RegisteringPersonRecord dto = createRegisteringPersonDTO();
        Address address = createAddress();
        address.setId(1L);

        Person savedPerson = createPerson();
        savedPerson.setId(1L);
        savedPerson.getAddressSet().add(address);

        when(personRepository.existsByCpf(dto.cpf())).thenReturn(true);

        // execution
        Throwable exception = catchThrowable(() -> personService.register(dto, address));

        // validation
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("CPF already registered");

        verify(personRepository, Mockito.never()).save(any(Person.class));
    }

    @Test
    @DisplayName("Validates if a person exists in database by passed 'CPF'")
    public void existsByCPFTest() {
        // scenery
        Person person = createPerson();

        when(personRepository.existsByCpf(person.getCpf())).thenReturn(true);

        // execution
        boolean result = personService.existsByCpf(person.getCpf());
        // validation
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should find a person by ID")
    public void findByIdTest() {
        // scenery
        Long id = 1L;
        Person person = createPerson();
        person.setId(id);

        when(personRepository.findById(id)).thenReturn(Optional.of(person));

        // execution
        Optional<Person> foundPerson = personService.findById(id);

        // validation
        assertThat(foundPerson.isPresent()).isTrue();
        assertThat(foundPerson.get().getId()).isEqualTo(id);
        assertThat(foundPerson.get().getName()).isEqualTo(person.getName());
        assertThat(foundPerson.get().getCpf()).isEqualTo(person.getCpf());
        assertThat(foundPerson.get().getBirthDay()).isEqualTo(person.getBirthDay());
        assertThat(foundPerson.get().getAddressSet()).isEqualTo(person.getAddressSet());

    }

    @Test
    @DisplayName("Should return an empty Optional when trying to find person by invalid id")
    public void findByInvalidIdTest() {
        // scenery
        Long id = 1L;

        when(personRepository.findById(id)).thenReturn(Optional.empty());
        // execution
        Optional<Person> foundPerson = personService.findById(id);

        // validation
        assertThat(foundPerson.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Should update a person")
    public void updateTest() {
        // scenery
        Person person = createPerson();
        UpdatingPersonRecord update = createUpdatingPersonDTO();

        when(personRepository.existsByCpf(person.getCpf())).thenReturn(true);
        when(personRepository.save(person)).thenReturn(person);

        // execution
        Person updatedPerson = personService.update(person, update);

        // validation
        assertThat(updatedPerson.getName()).isEqualTo(update.name());
        assertThat(updatedPerson.getBirthDay()).isEqualTo(update.birthDay());
        verify(personRepository, times(1)).save(person);
    }

    @Test
    @DisplayName("Should throw a BusinessException when trying to update an unsaved person")
    public void updateUnsavedPersonTest() {
        // scenery
        Person person = createPerson();
        UpdatingPersonRecord dto = createUpdatingPersonDTO();

        when(personRepository.existsByCpf(person.getCpf())).thenReturn(false);

        // execution
        Throwable exception = catchThrowable(() -> personService.update(person, dto));

        // validation
        assertThat(exception).isInstanceOf(BusinessException.class);

        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when trying to update person with an already saved CPF")
    public void updateAlreadySavedCPFTest() {
        // scenery
        Person person = createPerson();
        UpdatingPersonRecord update = createUpdatingPersonDTO();

        when(personRepository.existsByCpf(update.cpf())).thenReturn(true);
        when(personRepository.existsByCpf(person.getCpf())).thenReturn(true);
        when(personRepository.save(person)).thenReturn(person);

        // execution
        Throwable exception = catchThrowable(() -> personService.update(person, update));
        // validation
        assertThat(exception).isInstanceOf(BusinessException.class);
        assertThat(exception).hasMessage("Passed CPF belongs an already saved person");

        verify(personRepository, never()).save(person);
    }

    @Test
    @DisplayName("Should find person filtering by name ")
    public void findTest() {
        // scenery
        String name = createPerson().getName();
        Person person = createPerson();
        person.setId(1L);


        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Person> list = Collections.singletonList(person);

        PageImpl<Person> page = new PageImpl<>(list, pageRequest, 1);

        when(personRepository.findByName(any(String.class), any(Pageable.class))).thenReturn(page);

        // execution
        Page<Person> result = personService.find(name, pageRequest);

        // validation
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(list);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);

    }

    @Test
    @DisplayName("Should add an address to a person's set")
    public void addAddressTest() {
        // scenery
        Long id = 1L;
        Person person = createPerson();
        Address address = createAddress();

        person.setId(id);
        address.setId(id);

        when(personRepository.existsByCpf(person.getCpf())).thenReturn(true);
        when(addressRepository.save(address)).thenReturn(address);
        when(personRepository.save(person)).thenReturn(person);

        // execution
        Person updatedPerson = personService.addAddress(person, address);

        // validation
        assertThat(updatedPerson.getAddressSet().contains(address)).isTrue();

        verify(personRepository, times(1)).save(any(Person.class));
        verify(addressRepository, times(2)).save(any(Address.class));
    }

    @Test
    @DisplayName("Should throw a BusinessException when trying to add address to an unsaved Person")
    public void addAddressToInvalidPersonTest() {
        Long id = 1L;
        Person person = createPerson();
        Address address = createAddress();

        person.setId(id);
        address.setId(id);

        when(personRepository.existsByCpf(person.getCpf())).thenReturn(false);

        // execution
        Throwable exception = catchThrowable(() -> personService.addAddress(person, address));

        // validation
        assertThat(exception).isInstanceOf(BusinessException.class);

        verify(personRepository, never()).save(any(Person.class));
        verify(addressRepository, never()).save(any(Address.class));

    }

    @Test
    @DisplayName("Should set an address as main")
    public void setAddressAsMainTest() {
        // scenery
        Address address1 = createAddress();
        Address address2 = Address.builder().zipcode("22222222").build();

        address1.setMain(true);
        address2.setMain(false);

        Person person = createPerson();
        person.getAddressSet().add(address1);
        person.getAddressSet().add(address2);

        when(addressRepository.save(address1)).thenReturn(address1);
        when(addressRepository.save(address2)).thenReturn(address2);
        when(personRepository.save(person)).thenReturn(person);
        when(personRepository.existsByCpf(Mockito.any(String.class))).thenReturn(true);

        // execution
        Person result = personService.setAddressAsMain(person, address2);

        // validation
        assertThat(result.getAddressSet().contains(address1)).isTrue();
        assertThat(result.getAddressSet().contains(address2)).isTrue();

        assertThat(result.getAddressSet()
                .stream()
                .filter( a -> Objects.equals(a.getZipcode(), address1.getZipcode()))
                .toList()
                .get(0)
                .getMain()).isFalse();

        assertThat(result.getAddressSet()
                .stream()
                .filter( a -> Objects.equals(a.getZipcode(), address2.getZipcode()))
                .toList()
                .get(0)
                .getMain()).isTrue();

        verify(addressRepository, times(1)).save(any(Address.class));
        verify(personRepository, times(1)).save(any(Person.class));
    }

    @Test
    @DisplayName("Should throw an BusinessException when passed Address doesn't belong to passed Person")
    public void setInvalidAddressAsMainTest() {
        // scenery
        Person person = createPerson();
        Address address = createAddress();
        String message = "Passed address doesn't belong to passed person";

        when(personRepository.existsByCpf(Mockito.any(String.class))).thenReturn(true);
        // execution
        Throwable exception = catchThrowable(() -> personService.setAddressAsMain(person, address));

        // validation
        assertThat(exception).isInstanceOf(BusinessException.class);
        assertThat(exception).hasMessage(message);
    }

    @Test
    @DisplayName("Should throw a BusinessException when Person is not saved")
    public void setNullAddressAsMainTest() {
        // scenery
        Person person = createPerson();
        Address address = Address.builder().build();
        String message = "Person not saved";

        // execution
        Throwable exception = catchThrowable(() -> personService.setAddressAsMain(person, address));

        // validation
        assertThat(exception).isInstanceOf(BusinessException.class);
        assertThat(exception).hasMessage(message);
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
                .cpf("486.031.170-12")
                .birthDay(LocalDate.of(1998, 11, 25))
                .addressSet(new HashSet<>())
                .build();
    }

    private static RegisteringPersonRecord createRegisteringPersonDTO() {
        return new RegisteringPersonRecord("Fulano", "486.031.170-12", LocalDate.of(1998, 11, 25), "12345678", 123 );
    }

    private static UpdatingPersonRecord createUpdatingPersonDTO() {
        return new UpdatingPersonRecord("Sicrano", LocalDate.of(2002, 11, 22), "252.916.820-27");
    }
}
