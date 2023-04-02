package br.com.itstoony.attornatus.dto;

import br.com.itstoony.attornatus.model.Address;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PersonDTO {

    private Long id;

    private String name;

    private String cpf;

    private LocalDate birthDay;

    private Set<Address> addressSet;

}
