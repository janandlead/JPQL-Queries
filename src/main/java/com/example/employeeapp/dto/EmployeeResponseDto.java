package com.example.employeeapp.dto;

import java.math.BigDecimal;

public record EmployeeResponseDto(
        Long id,
        String name,
        String email,
        BigDecimal salary,
        Long departmentId,
        String departmentName) {
}
