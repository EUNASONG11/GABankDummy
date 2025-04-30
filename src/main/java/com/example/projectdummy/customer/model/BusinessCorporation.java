package com.example.projectdummy.customer.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessCorporation {
    private Long custId;
    private String businessNumber;
    private String companyName;
    private String fax;
    private String corporationNumber;
}
