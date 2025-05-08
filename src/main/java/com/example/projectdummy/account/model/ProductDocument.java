package com.example.projectdummy.account.model;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class ProductDocument {
    private long productDocumentId;
    private long productId;
    private long documentId;
}
