package com.example.employeeapp.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.employeeapp.dto.EmployeeRequestDto;
import com.example.employeeapp.dto.EmployeeResponseDto;
import com.example.employeeapp.entity.Department;
import com.example.employeeapp.entity.Employee;
import com.example.employeeapp.exception.DepartmentNotFoundException;
import com.example.employeeapp.exception.DuplicateEmployeeException;
import com.example.employeeapp.exception.EmployeeNotFoundException;
import com.example.employeeapp.repository.DepartmentRepository;
import com.example.employeeapp.repository.EmployeeRepository;
import com.example.employeeapp.service.EmployeeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public EmployeeResponseDto addEmployee(EmployeeRequestDto request) {
        if (employeeRepository.existsByEmail(request.email())) {
            throw new DuplicateEmployeeException("Employee email already exists: " + request.email());
        }

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new DepartmentNotFoundException(
                        "Department not found: " + request.departmentId()));

        Employee employee = new Employee(null, request.name(), request.email(), request.salary(), department);
        return toResponse(employeeRepository.save(employee));
    }

    @Override
    public EmployeeResponseDto getEmployeeByEmail(String email) {
        return employeeRepository.findEmployeeByEmail(email)
                .map(this::toResponse)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found for email: " + email));
    }

    @Override
    public EmployeeResponseDto getEmployeeByEmailPosition(String email) {
        return employeeRepository.findEmployeeByEmailPosition(email)
                .map(this::toResponse)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found for email: " + email));
    }

    @Override
    public List<EmployeeResponseDto> getEmployeesByDepartmentAndSalary(Long departmentId, BigDecimal minSalary) {
        ensureDepartmentExists(departmentId);
        return map(employeeRepository.findEmployeesByDepartmentAndSalary(departmentId, minSalary));
    }

    @Override
    public List<EmployeeResponseDto> getEmployeesByDepartmentAndSalaryPosition(Long departmentId, BigDecimal minSalary) {
        ensureDepartmentExists(departmentId);
        return map(employeeRepository.findEmployeesByDepartmentAndSalaryPosition(departmentId, minSalary));
    }

    @Override
    public List<EmployeeResponseDto> getEmployeesByDepartmentName(String departmentName) {
        return map(employeeRepository.findEmployeesByDepartmentName(departmentName));
    }

    @Override
    public List<EmployeeResponseDto> getEmployeesByDepartmentNamePosition(String departmentName) {
        return map(employeeRepository.findEmployeesByDepartmentNamePosition(departmentName));
    }

    @Override
    public List<EmployeeResponseDto> searchEmployees(String departmentName, BigDecimal salary, String employeeName) {
        return map(employeeRepository.searchEmployees(departmentName, salary, employeeName));
    }

    @Override
    public List<EmployeeResponseDto> searchEmployeesUsingIlike(String departmentName, String employeeName) {
        return map(employeeRepository.searchEmployeesUsingIlike(departmentName, employeeName));
    }

    private void ensureDepartmentExists(Long departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new DepartmentNotFoundException("Department not found: " + departmentId);
        }
    }

    private List<EmployeeResponseDto> map(List<Employee> employees) {
        return employees.stream().map(this::toResponse).toList();
    }

    private EmployeeResponseDto toResponse(Employee employee) {
        Department department = employee.getDepartment();
        return new EmployeeResponseDto(employee.getId(), employee.getName(), employee.getEmail(), employee.getSalary(),
                department == null ? null : department.getId(), department == null ? null : department.getName());
    }
}
