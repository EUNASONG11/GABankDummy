package com.example.projectdummy.account.model;


import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class ContractDocument {
    private Long contractDocumentId;
    private long contractId;
    private long productDocumentId;
    private String document;
    private String productCode;
}
