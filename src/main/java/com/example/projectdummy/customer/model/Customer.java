package com.example.projectdummy.customer.model;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class Customer {
    private Long custId;
    private String custName;
    private String phone;
    private String email;
    private String brith;
    private String custCode;
    private int creditPoint;
}
