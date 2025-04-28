package com.example.projectdummy.account.model;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class BankAccount {
    private Long accountId;
    private Long productId;
    private Long custId;
    private Long employeeId;
    private String accountNum;
    private String accountPassword;
    private int money;
    private String statusCode;
}
