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
import java.util.Set;

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
        return personRepository.findByName(name, pageable);
    }

    public Person addAddress(Person person, Address address) {
        if (!existsByCpf(person.getCpf())) {
            throw new BusinessException("Cannot add address to an unsaved Person");
        }

        addressRepository.save(address);

        person.getAddressSet().add(address);

        return personRepository.save(person);
    }

    public Person setAddressAsMain(Person person, Address address) {
        if (!existsByCpf(person.getCpf())) {
            throw new BusinessException("Person not saved");
        }

        if (!person.getAddressSet().contains(address)) {
            throw new BusinessException("Passed address doesn't belong to passed person");
        }

        Set<Address> addressSet = person.getAddressSet();
        addressSet.forEach(a -> a.setMain(false));

        address.setMain(true);

        addressRepository.save(address);
        return personRepository.save(person);
    }

    public boolean existsByCpf(String cpf) {
        return personRepository.existsByCpf(cpf);
    }

}
