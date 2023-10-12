package com.example.mscreditassessor.infra.clients;

import com.example.mscreditassessor.domain.model.CardPerClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "mscards", path = "/cards")
public interface CardResourceClient {
    @GetMapping(params = "cpf")
    ResponseEntity<List<CardPerClient>> getCardByClient(@RequestParam("cpf") String cpf);
}
