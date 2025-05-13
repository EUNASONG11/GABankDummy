package com.example.projectdummy.account.model;


import lombok.Builder;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Setter
public class ContractDocument {
    private Long contractDocumentId;
    private long contractId;
    private long productDocumentId;
    private String document;
    private String productCode;
    private LocalDateTime createdAt;
}
