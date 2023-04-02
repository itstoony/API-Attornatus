package br.com.itstoony.attornatus.repository;

import br.com.itstoony.attornatus.model.Address;
import br.com.itstoony.attornatus.model.Person;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {


    Page<Address> findByPerson(@Param("person") Person person, Pageable pageable);
}
