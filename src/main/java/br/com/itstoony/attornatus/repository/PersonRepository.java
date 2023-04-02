package br.com.itstoony.attornatus.repository;

import br.com.itstoony.attornatus.model.Person;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Boolean existsByCpf(String cpf);

    @Query("SELECT p FROM Person p WHERE lower(p.name) like lower(concat('%', :name, '%'))")
    Page<Person> findByName(@Param("name") String name, Pageable pageable);


}
