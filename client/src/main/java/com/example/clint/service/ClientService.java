package com.example.clint.service;

import com.example.clint.domain.Client;
import com.example.clint.infra.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    @Transactional
    public Client save(Client client){
        return clientRepository.save(client);
    }

    public Optional<Client> getByCpf(String cpf){
        return clientRepository.getByCpf(cpf);
    }
}
