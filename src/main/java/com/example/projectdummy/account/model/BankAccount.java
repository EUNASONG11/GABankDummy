package com.example.projectdummy.account.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
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
    private LocalDateTime createdAt;
}
