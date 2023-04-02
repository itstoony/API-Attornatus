package br.com.itstoony.attornatus.model;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Address {

    private Long id;

    private String street;

    private String zipcode;

    private Integer number;

    private String city;

    private Boolean main;

}
