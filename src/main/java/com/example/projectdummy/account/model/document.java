package com.example.projectdummy.account.model;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class document {
    private Long documentId;
    private String documentName;
    private String document;
}
