package com.example.clint.infra.repository;

import com.example.clint.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> getByCpf(String cpf);
}
