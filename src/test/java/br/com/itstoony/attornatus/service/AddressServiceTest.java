package br.com.itstoony.attornatus.service;

import br.com.itstoony.attornatus.client.ViaCepClient;
import br.com.itstoony.attornatus.dto.RegisteringPersonRecord;
import br.com.itstoony.attornatus.dto.ViaCepResponseDTO;
import br.com.itstoony.attornatus.exception.BusinessException;
import br.com.itstoony.attornatus.model.Address;
import br.com.itstoony.attornatus.model.Person;
import br.com.itstoony.attornatus.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class AddressServiceTest {

    AddressService addressService;

    @MockBean
    AddressRepository addressRepository;

    @MockBean
    ViaCepClient viaCepClient;

    @BeforeEach
    public void setUp() {
        this.addressService = new AddressService(viaCepClient, addressRepository);
    }

    @Test
    @DisplayName("Should find an address by RegisteringPersonDTO")
    public void findByRegisteringDTO() {
        // scenery
        RegisteringPersonRecord dto = createRegisteringPersonDTO();
        Address savedAddress = createAddress();

        BDDMockito.when(viaCepClient.getZipcode(dto.zipcode())).thenReturn(createViaCepResponse());
        when(addressRepository.save(any(Address.class))).thenReturn(savedAddress);

        // execution
        Address address = addressService.findFromDTO(createRegisteringPersonDTO());

        // validation
        assertThat(address.getZipcode()).isEqualTo(dto.zipcode());
        assertThat(address.getNumber()).isEqualTo(createRegisteringPersonDTO().number());
        assertThat(address.getCity()).isEqualTo(createAddress().getCity());
        assertThat(address.getStreet()).isEqualTo(createAddress().getStreet());
        assertThat(address.getMain()).isFalse();

        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    @DisplayName("Should throw a BusinessException when trying to find address without enough data")
    public void findByInvalidRegisteringDTO() {
        // scenery
        RegisteringPersonRecord dto = new RegisteringPersonRecord("", "", null, "", null);

        // execution
        Throwable exception = catchThrowable(() -> addressService.findFromDTO(dto));

        // validation
        assertThat(exception).isInstanceOf(BusinessException.class);

        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    @DisplayName("Should return a page of all addresses from a person")
    public void findAllAddressTest() {
        // scenery
        Person person = createPerson();
        Address address = createAddress();

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Address> list = Collections.singletonList(address);

        PageImpl<Address> page = new PageImpl<>(list, pageRequest, 1);

        when(addressRepository.findByPerson(any(Person.class), any(Pageable.class))).thenReturn(page);

        // execution
        Page<Address> result = addressService.findAllAddress(person, pageRequest);

        // validation
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(list);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
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

    private static RegisteringPersonRecord createRegisteringPersonDTO() {
        return new RegisteringPersonRecord("Fulano", "486.031.170-12", LocalDate.of(1998, 11, 25), "69098384", 123 );
    }

    private static ViaCepResponseDTO createViaCepResponse() {
        return ViaCepResponseDTO.builder()
                .cep("69098384")
                .localidade("Manaus")
                .logradouro("Rua Hortelã-do-Campo")
                .build();
    }

}
