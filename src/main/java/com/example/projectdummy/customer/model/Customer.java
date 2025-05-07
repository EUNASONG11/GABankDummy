package com.example.projectdummy.customer.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Customer {
    private Long custId;
    private String custName;
    private String phone;
    private String email;
    private String birth;
    private String custCode;
    private int creditPoint;
}
