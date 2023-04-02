package br.com.itstoony.attornatus.service;

import br.com.itstoony.attornatus.model.Address;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddressService {

    public Address findByZipcode(String zipcode) {
        return null;
    }

    public Optional<Address> findById(Long id) {
        return Optional.empty();
    }
}
