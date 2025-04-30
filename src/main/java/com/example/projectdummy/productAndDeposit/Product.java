package com.example.projectdummy.productAndDeposit;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class Product {
    private Long productId;
    private String productCode;
}
