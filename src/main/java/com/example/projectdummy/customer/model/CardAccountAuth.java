package com.example.projectdummy.customer.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class CardAccountAuth {
    private Long divisionId;
    private String location;
    private Integer state;
    private LocalDateTime createdAt;
    private Integer flag;
    private Integer authFlag;
}
