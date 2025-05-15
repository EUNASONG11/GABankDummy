package com.example.projectdummy.account.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CheckBillInfo {
    private Long accountId;
    private LocalDateTime createdAt;
}
