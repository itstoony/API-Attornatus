package br.com.itstoony.attornatus.client;

import br.com.itstoony.attornatus.dto.ViaCepResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(url = "https://viacep.com.br/ws", name = "viacep")
public interface ViaCepClient {

    @GetMapping("/{zipcode}/json")
    ViaCepResponseDTO getZipcode(@PathVariable("zipcode") String zipcode);

}
