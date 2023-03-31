package br.com.itstoony.attornatus.model;

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
public class Person {

    private Long id;

    private String name;

    private LocalDate birthDay;

    private Set<Address> addressSet;

}
