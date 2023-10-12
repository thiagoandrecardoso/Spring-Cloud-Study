package com.example.mscreditassessor.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientSituation {
    private ClientData clientData;
    private List<CardPerClient> cardPerClientList;
}
