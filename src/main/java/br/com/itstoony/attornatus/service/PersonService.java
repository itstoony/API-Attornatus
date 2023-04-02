package br.com.itstoony.attornatus.service;

import br.com.itstoony.attornatus.dto.RegisteringPersonRecord;
import br.com.itstoony.attornatus.dto.UpdatingPersonRecord;
import br.com.itstoony.attornatus.exception.BusinessException;
import br.com.itstoony.attornatus.model.Address;
import br.com.itstoony.attornatus.model.Person;
import br.com.itstoony.attornatus.repository.AddressRepository;
import br.com.itstoony.attornatus.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    private final AddressRepository addressRepository;


    public Person register(RegisteringPersonRecord dto, Address address) {

        if (existsByCpf(dto.cpf())) {
            throw new BusinessException("CPF already registered");
        }

        address.setMain(true);
        Person savingPerson = Person.builder()
                .id(null)
                .name(dto.name())
                .birthDay(dto.birthDay())
                .addressSet(new HashSet<>(List.of(address)))
                .build();

        addressRepository.save(address);

        return personRepository.save(savingPerson);
    }

    public Optional<Person> findById(Long id) {
        return personRepository.findById(id);
    }

    public Person update(Person person, UpdatingPersonRecord update) {
        if (!existsByCpf(person.getCpf())) {
            throw new BusinessException("Cannot update an unsaved person");
        }

        if (update.name() != null && !update.name().isBlank()) {
            person.setName(update.name());
        }

        if (update.birthDay() != null) {
            person.setBirthDay(update.birthDay());
        }

        return personRepository.save(person);
    }

    public Page<Person> find(String name, Pageable pageable) {
        return null;
    }

    public Person addAddress(Person person, Address address) {
        return null;
    }

    public Page<Address> findAllAddress(Person person, Pageable pageable) {
        return null;
    }

    public Person setAddressAsMain(Person person, Address address) {
        return null;
    }

    public boolean existsByCpf(String cpf) {
        return personRepository.existsByCpf(cpf);
    }

}
