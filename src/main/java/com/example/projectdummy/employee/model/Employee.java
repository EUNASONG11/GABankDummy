package com.example.projectdummy.employee.model;

import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class Employee {
    private Long employeeId;
    private Long branchId;
    private Long teamId;
    private String empName;
    private String phone;
    private String empCode;
    private String positionCode;
    private String departmentsCode;
}
