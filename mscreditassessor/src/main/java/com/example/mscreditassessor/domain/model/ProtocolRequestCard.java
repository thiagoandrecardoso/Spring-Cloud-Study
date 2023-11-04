package com.example.mscreditassessor.domain.model;

import lombok.Data;

@Data
public class ProtocolRequestCard {
    private String protocol;
    public ProtocolRequestCard(String protocol) {
        this.protocol = protocol;
    }
}
