package br.com.itstoony.attornatus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class RegisteringPersonDTO {

    private String name;

    private LocalDate birthDay;

    private String zipcode;

}
