package com.example.mscreditassessor.controller;

import com.example.mscreditassessor.domain.model.CardIssueData;
import com.example.mscreditassessor.domain.model.ClientSituation;
import com.example.mscreditassessor.domain.model.ProtocolRequestCard;
import com.example.mscreditassessor.exceptions.ClientDataNotFoundException;
import com.example.mscreditassessor.exceptions.ErrorCommunicateMicroServicesException;
import com.example.mscreditassessor.exceptions.ErrorRequestCardIssueException;
import com.example.mscreditassessor.service.CreditAssessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("credit-assessor")
public class CreditAssessorController {

    private final CreditAssessorService creditAssessorService;

    @GetMapping
    public String status(){
        return "OK";
    }

    @GetMapping(value = "client-situation", params = "cpf")
    public ResponseEntity<?> consultClientSituation(@RequestParam("cpf") String cpf){
        try{
            ClientSituation clientSituation = creditAssessorService.getClientSituation(cpf);
            return ResponseEntity.ok(clientSituation);
        } catch (ClientDataNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (ErrorCommunicateMicroServicesException e){
            HttpStatus httpStatus = HttpStatus.resolve(e.getStatus());
            return ResponseEntity.status(httpStatus != null ? httpStatus : HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("request-card")
    public ResponseEntity requestCard(@RequestBody CardIssueData data) {
        try{
            ProtocolRequestCard protocolRequestCard = creditAssessorService.requestCardIssue(data);
            return ResponseEntity.ok(protocolRequestCard);
        }catch (ErrorRequestCardIssueException ex){
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

}
