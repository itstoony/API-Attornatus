package br.com.itstoony.attornatus.repository;

import br.com.itstoony.attornatus.model.Address;
import br.com.itstoony.attornatus.model.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class AddressRepositoryTest {

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    PersonRepository personRepository;

    @Test
    @DisplayName("Should return all addresses from a person")
    public void findByPersonTest() {
        // scenery
        Address address = createAddress();
        Person person = createPerson();
        personRepository.save(person);

        address.setPerson(person);
        Address savedAddress = addressRepository.save(address);

        person.getAddressSet().add(address);
        personRepository.save(person);

        // execution
        Page<Address> result = addressRepository.findByPerson(person, PageRequest.of(0, 10));

        // validation
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).contains(savedAddress);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(1);
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


    private static Address createAddress() {
        return Address.builder()
                .id(1L)
                .street("Rua Hortelã-do-Campo")
                .zipcode("69098384")
                .number(123)
                .city("Manaus")
                .main(false)
                .build();
    }
}
