package br.com.itstoony.attornatus.repository;

import br.com.itstoony.attornatus.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

}
