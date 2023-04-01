package br.com.itstoony.attornatus.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AddressRecord(
        @NotEmpty(message = "Zipcode cannot be empty")
        @Pattern(regexp = "\\d{5}\\d{3}", message = "zipcode pattern should be: '99999999'")
        String zipcode,

        @NotNull(message = "Number cannot be null")
        Integer number) { }
