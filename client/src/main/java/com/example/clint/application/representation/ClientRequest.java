package com.example.clint.application.representation;

import com.example.clint.domain.Client;
import lombok.Data;

@Data
public class ClientRequest {
    private String cpf;
    private String name;
    private Integer age;

    public Client toModel(){
        return new Client(cpf, name, age);
    }
}
