package com.example.mscreditassessor.exceptions;

public class ClientDataNotFoundException extends Exception{
    public ClientDataNotFoundException() {
        super("Client data not found for the CPF entered.");
    }
}
