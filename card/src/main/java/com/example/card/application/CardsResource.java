package com.example.card.application;

import com.example.card.application.dto.CardSaveRequest;
import com.example.card.application.dto.ClientCardResponse;
import com.example.card.application.service.CardService;
import com.example.card.application.service.ClientCardService;
import com.example.card.domain.Card;
import com.example.card.domain.ClientCard;
import com.example.card.infra.repository.ClientCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("cards")
@RequiredArgsConstructor
public class CardsResource {
    private final CardService cardService;

    private final ClientCardService clientCardService;

    @GetMapping
    public String status(){
        return "OK";
    }

    @PostMapping
    public ResponseEntity<Card> create(@RequestBody CardSaveRequest request){
        Card card = request.toModel();
        cardService.save(card);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(params = "rent")
    public ResponseEntity<List<Card>> getCardsWithRentLessThanOrEqualTo(@RequestParam("rent") Long rent){
        List<Card> cardList = cardService.getCardsWithRentLessThanOrEqualTo(rent);
        return ResponseEntity.ok(cardList);
    }

    @GetMapping(params = "cpf")
    public ResponseEntity<List<ClientCardResponse>> getCardByClient(@RequestParam("cpf") String cpf) {
        List<ClientCard> list = clientCardService.listCardByCpf(cpf);
        List<ClientCardResponse> resultList = list.stream()
                .map(ClientCardResponse::fromModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resultList);
    }
}
