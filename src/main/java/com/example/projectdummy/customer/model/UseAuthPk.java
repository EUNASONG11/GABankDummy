package com.example.projectdummy.customer.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class UseAuthPk {
    private Long accountId;
    private Long productId;
    private LocalDateTime createdAt;
}
