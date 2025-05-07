package com.example.projectdummy.account.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BankInternalAccount {
    private Long accountId;
    private String accountCode;
    private String accountNum;
    private Long money;
    private LocalDate createAt;
    private LocalDate updateAt;
}
