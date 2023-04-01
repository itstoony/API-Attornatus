package br.com.itstoony.attornatus.service;

import br.com.itstoony.attornatus.dto.RegisteringPersonRecord;
import br.com.itstoony.attornatus.dto.UpdatingPersonRecord;
import br.com.itstoony.attornatus.model.Address;
import br.com.itstoony.attornatus.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonService {

    public Person register(RegisteringPersonRecord dto, Address address) {
        return null;
    }

    public Optional<Person> findById(Long id) {
        return Optional.empty();
    }

    public Person update(Person person, UpdatingPersonRecord update) {
        return null;
    }

    public Page<Person> find(String name, Pageable pageable) {
        return null;
    }
}
