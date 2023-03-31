package br.com.itstoony.attornatus.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private Long id;

    private String street;

    private String zipcode;

    private Integer number;

    private String city;

}
