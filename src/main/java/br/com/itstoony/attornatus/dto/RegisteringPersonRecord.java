package br.com.itstoony.attornatus.dto;

import java.time.LocalDate;

public record RegisteringPersonRecord(
     String name,
     LocalDate birthDay,
     String zipcode
) { }
