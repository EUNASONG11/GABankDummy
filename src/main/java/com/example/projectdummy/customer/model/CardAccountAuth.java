package com.example.projectdummy.customer.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CardAccountAuth {
    private Long division_id;
    private String location;
    private Integer state;
    private LocalDate createdAt;
    private Integer flag;
}
