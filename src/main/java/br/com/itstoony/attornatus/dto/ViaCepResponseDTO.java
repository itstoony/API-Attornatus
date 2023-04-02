package br.com.itstoony.attornatus.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ViaCepResponseDTO {

    private String cep;
    private String logradouro;
    private String localidade;

}
