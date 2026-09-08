package com.example.employeeapp.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EmployeeRequestDto(@NotBlank(message = "Name is required") String name,
		@NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
		@NotNull(message = "Salary is required") @Positive(message = "Salary must be greater than zero") BigDecimal salary,
		@NotNull(message = "Department ID is required") Long departmentId) {
}
