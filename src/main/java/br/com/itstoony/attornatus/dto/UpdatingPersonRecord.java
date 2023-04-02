package br.com.itstoony.attornatus.dto;

import java.time.LocalDate;

public record UpdatingPersonRecord(String name, LocalDate birthDay, String cpf) { }
