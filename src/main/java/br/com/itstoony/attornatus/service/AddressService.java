package br.com.itstoony.attornatus.service;

import br.com.itstoony.attornatus.client.ViaCepClient;
import br.com.itstoony.attornatus.dto.AddressRecord;
import br.com.itstoony.attornatus.dto.RegisteringPersonRecord;
import br.com.itstoony.attornatus.dto.ViaCepResponseDTO;
import br.com.itstoony.attornatus.exception.BusinessException;
import br.com.itstoony.attornatus.model.Address;
import br.com.itstoony.attornatus.model.Person;
import br.com.itstoony.attornatus.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final ViaCepClient client;

    private final AddressRepository addressRepository;


    public Address findFromDTO(RegisteringPersonRecord dto) {
        validateDTO(dto.zipcode(), dto.number());

        ViaCepResponseDTO response = client.getZipcode(dto.zipcode());
        Address address = Address.builder()
                .zipcode(response.getCep())
                .street(response.getLogradouro())
                .city(response.getLocalidade())
                .number(dto.number())
                .main(false)
                .build();

        return addressRepository.save(address);
    }

    public Address findFromDTO(AddressRecord record) {
        validateDTO(record.zipcode(), record.number());

        ViaCepResponseDTO response = client.getZipcode(record.zipcode());
        Address address = Address.builder()
                .zipcode(response.getCep())
                .street(response.getLogradouro())
                .city(response.getLocalidade())
                .number(record.number())
                .main(false)
                .build();

        return addressRepository.save(address);
    }

    private static void validateDTO(String zipcode, Integer number) {
        if (zipcode.isBlank() || number == null) {
            throw new BusinessException("Zipcode and Number must not be empty or null");
        }
    }

    public Page<Address> findAllAddress(Person person, Pageable pageable) {
        return addressRepository.findByPerson(person, pageable);
    }

    public Person setAddressAsMain(Address address) {
        return null;
    }

    public Optional<Address> findById(Long id) {
        return Optional.empty();
    }
}
