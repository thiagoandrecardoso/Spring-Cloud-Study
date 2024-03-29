package com.example.clint.application;

import com.example.clint.application.representation.ClientRequest;
import com.example.clint.domain.Client;
import com.example.clint.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("clients")
@RequiredArgsConstructor
public class ClientResource {
    private static final Logger log = LoggerFactory.getLogger(ClientResource.class);
    private final ClientService clientService;

    @GetMapping
    public String status(){
        log.info("Getting microservices status from clients!");
        return "OK";
    }

    @PostMapping
    public ResponseEntity<Client> save(@RequestBody ClientRequest clientRequest){
        var client = clientRequest.toModel();
        clientService.save(client);
        URI headerLocation = ServletUriComponentsBuilder.fromCurrentRequest()
                .query("cpf={cpf}")
                .buildAndExpand(client.getCpf())
                .toUri();
        return ResponseEntity.created(headerLocation).build();
    }

    @GetMapping(params = "cpf")
    public ResponseEntity<Client> getClient(@RequestParam("cpf") String cpf){
        var client = clientService.getByCpf(cpf);
        return client.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
