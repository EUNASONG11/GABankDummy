package com.example.projectdummy.account.model;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class BankInternalAccount {
    private Long accountId;
    private String accountCode;
    private String accountNum;
    private Long money;
}
