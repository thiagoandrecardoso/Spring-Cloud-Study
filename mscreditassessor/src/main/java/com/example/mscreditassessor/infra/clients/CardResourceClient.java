package com.example.mscreditassessor.infra.clients;

import com.example.mscreditassessor.domain.model.ApprovedCard;
import com.example.mscreditassessor.domain.model.Card;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "mscards", path = "/cards")
public interface CardResourceClient {
    @GetMapping(params = "cpf")
    ResponseEntity<List<ApprovedCard>> getCardByClient(@RequestParam("cpf") String cpf);

    @GetMapping(params = "rent")
    ResponseEntity<List<Card>> getCardRevenueTo(@RequestParam("rent") Long rent);
}
