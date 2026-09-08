package com.example.employeeapp.service;

import java.math.BigDecimal;
import java.util.List;

import com.example.employeeapp.dto.EmployeeRequestDto;
import com.example.employeeapp.dto.EmployeeResponseDto;

public interface EmployeeService {
    EmployeeResponseDto addEmployee(EmployeeRequestDto request);
    EmployeeResponseDto getEmployeeByEmail(String email);
    EmployeeResponseDto getEmployeeByEmailPosition(String email);
    List<EmployeeResponseDto> getEmployeesByDepartmentAndSalary(Long departmentId, BigDecimal minSalary);
    List<EmployeeResponseDto> getEmployeesByDepartmentAndSalaryPosition(Long departmentId, BigDecimal minSalary);
    List<EmployeeResponseDto> getEmployeesByDepartmentName(String departmentName);
    List<EmployeeResponseDto> getEmployeesByDepartmentNamePosition(String departmentName);
    List<EmployeeResponseDto> searchEmployees(String departmentName, BigDecimal salary, String employeeName);
    List<EmployeeResponseDto> searchEmployeesUsingIlike(String departmentName, String employeeName);
}
