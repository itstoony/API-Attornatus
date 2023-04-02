package br.com.itstoony.attornatus.service;

import br.com.itstoony.attornatus.dto.RegisteringPersonRecord;
import br.com.itstoony.attornatus.model.Address;
import br.com.itstoony.attornatus.model.Person;
import br.com.itstoony.attornatus.repository.AddressRepository;
import br.com.itstoony.attornatus.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

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

        BDDMockito.when(addressRepository.save(Mockito.any(Address.class)))
                .thenReturn(address);
        BDDMockito.when(personRepository.save(Mockito.any(Person.class)))
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
    @DisplayName("Validates if a person exists in database by passed 'CPF'")
    public void existsByCPFTest() {
        // scenery
        Person person = createPerson();

        Mockito.when(personRepository.existsByCpf(person)).thenReturn(true);

        // execution
        boolean result = personService.existsByCpf(person);
        // validation
        assertThat(result).isTrue();
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

}
