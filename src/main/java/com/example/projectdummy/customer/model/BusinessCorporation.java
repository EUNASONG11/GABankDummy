package com.example.projectdummy.customer.model;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class BusinessCorporation {
    private Long custId;
    private String businessNumder;
    private String companyName;
    private String fax;
    private String corporationNumber;
}
