package br.com.itstoony.attornatus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PersonDTO {

    private Long id;

    private String name;

    private LocalDate birthDay;

    private Set<AddressDTO> addressSet;

}
