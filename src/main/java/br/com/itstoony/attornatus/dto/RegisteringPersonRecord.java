package br.com.itstoony.attornatus.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record RegisteringPersonRecord(
     @NotEmpty(message = "Name must not be empty") String name,
     @NotNull(message = "Birthday must not be null")  LocalDate birthDay,
     @NotEmpty @Pattern(regexp = "\\d{5}\\d{3}", message = "must be in 'CEP' pattern '99999999'") String zipcode
) { }
