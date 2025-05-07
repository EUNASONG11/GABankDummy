package com.example.projectdummy.productAndDeposit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class Product {
    private Long productId;
    private String productCode;
}
