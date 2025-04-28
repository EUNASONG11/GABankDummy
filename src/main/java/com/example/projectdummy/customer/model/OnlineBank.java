package com.example.projectdummy.customer.model;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class OnlineBank {
    private Long custId;
    private String id;
    private String pw;
    private String document;
}
