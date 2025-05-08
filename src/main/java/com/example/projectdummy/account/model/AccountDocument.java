package com.example.projectdummy.account.model;


import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class AccountDocument {
    private Long accountDocumentId;
    private long accountId;
    private long productDocumentId;
    private String document;
}
