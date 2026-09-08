package com.example.employeeapp.controller;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.employeeapp.dto.EmployeeRequestDto;
import com.example.employeeapp.dto.EmployeeResponseDto;
import com.example.employeeapp.service.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employees", description = "Employee and PostgreSQL native SQL examples")
public class EmployeeController {

	private final EmployeeService employeeService;

	@PostMapping
	@Operation(summary = "Add an employee")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Employee created", content = @Content(schema = @Schema(implementation = EmployeeResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Validation error"),
			@ApiResponse(responseCode = "404", description = "Department not found"),
			@ApiResponse(responseCode = "409", description = "Duplicate email") })
	public ResponseEntity<EmployeeResponseDto> addEmployee(@Valid @RequestBody EmployeeRequestDto request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.addEmployee(request));
	}

	@GetMapping("/email")
	@Operation(summary = "Find an employee by email using a named parameter")
	public EmployeeResponseDto byEmail(@RequestParam String email) {
		return employeeService.getEmployeeByEmail(email);
	}

	@GetMapping("/email/position")
	@Operation(summary = "Find an employee by email using a positional parameter")
	public EmployeeResponseDto byEmailPosition(@RequestParam String email) {
		return employeeService.getEmployeeByEmailPosition(email);
	}

	@GetMapping("/department/{departmentId}")
	@Operation(summary = "Find employees by department ID and minimum salary using named parameters")
	public List<EmployeeResponseDto> byDepartmentAndSalary(@PathVariable Long departmentId,
			@RequestParam BigDecimal minSalary) {
		return employeeService.getEmployeesByDepartmentAndSalary(departmentId, minSalary);
	}

	@GetMapping("/department/{departmentId}/position")
	@Operation(summary = "Find employees by department ID and minimum salary using positional parameters")
	public List<EmployeeResponseDto> byDepartmentAndSalaryPosition(@PathVariable Long departmentId,
			@RequestParam BigDecimal minSalary) {
		return employeeService.getEmployeesByDepartmentAndSalaryPosition(departmentId, minSalary);
	}

	@GetMapping("/department-name/{departmentName}")
	@Operation(summary = "Find employees by department name using a named parameter")
	public List<EmployeeResponseDto> byDepartmentName(@PathVariable String departmentName) {
		return employeeService.getEmployeesByDepartmentName(departmentName);
	}

	@GetMapping("/department-name/{departmentName}/position")
	@Operation(summary = "Find employees by department name using a positional parameter")
	public List<EmployeeResponseDto> byDepartmentNamePosition(@PathVariable String departmentName) {
		return employeeService.getEmployeesByDepartmentNamePosition(departmentName);
	}

	@GetMapping("/search")
	@Operation(summary = "Search by department, salary, and employee name")
	public List<EmployeeResponseDto> search(@RequestParam String departmentName, @RequestParam BigDecimal salary,
			@RequestParam String employeeName) {
		return employeeService.searchEmployees(departmentName, salary, employeeName);
	}

	@GetMapping("/search-ilike")
	@Operation(summary = "PostgreSQL case-insensitive search using ILIKE")
	public List<EmployeeResponseDto> searchIlike(@RequestParam String departmentName,
			@RequestParam String employeeName) {
		return employeeService.searchEmployeesUsingIlike(departmentName, employeeName);
	}
}
