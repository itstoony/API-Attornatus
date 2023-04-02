package br.com.itstoony.attornatus.repository;

import br.com.itstoony.attornatus.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
