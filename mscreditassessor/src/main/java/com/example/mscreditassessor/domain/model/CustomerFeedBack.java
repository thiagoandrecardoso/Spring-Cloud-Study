package com.example.mscreditassessor.domain.model;

import lombok.Data;

import java.util.List;

@Data
public class CustomerFeedBack {
    private List<ApprovedCard> approvedCardList;

    public CustomerFeedBack(List<ApprovedCard> listApprovedCard) {
        this.approvedCardList = listApprovedCard;
    }
}
