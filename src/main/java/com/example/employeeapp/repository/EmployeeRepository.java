package com.example.employeeapp.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.employeeapp.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);

    @Query("""
            SELECT e
            FROM Employee e
            WHERE e.email = :email
            """)
    Optional<Employee> findEmployeeByEmail(@Param("email") String email);

    @Query("""
            SELECT e
            FROM Employee e
            WHERE e.email = ?1
            """)
    Optional<Employee> findEmployeeByEmailPosition(String email);

    @Query(value = """
            SELECT * FROM employee
            WHERE department_id = :departmentId AND salary >= :minSalary
            """, nativeQuery = true)
    List<Employee> findEmployeesByDepartmentAndSalary(
            @Param("departmentId") Long departmentId,
            @Param("minSalary") BigDecimal minSalary);

    @Query(value = """
            SELECT * FROM employee
            WHERE department_id = ?1 AND salary >= ?2
            """, nativeQuery = true)
    List<Employee> findEmployeesByDepartmentAndSalaryPosition(Long departmentId, BigDecimal minSalary);

    @Query(value = """
            SELECT e.* FROM employee e
            JOIN department d ON e.department_id = d.id
            WHERE d.name = :departmentName
            """, nativeQuery = true)
    List<Employee> findEmployeesByDepartmentName(@Param("departmentName") String departmentName);

    @Query(value = """
            SELECT e.* FROM employee e
            JOIN department d ON e.department_id = d.id
            WHERE d.name = ?1
            """, nativeQuery = true)
    List<Employee> findEmployeesByDepartmentNamePosition(String departmentName);

    @Query(value = """
            SELECT e.* FROM employee e
            JOIN department d ON e.department_id = d.id
            WHERE d.name = :departmentName
              AND e.salary >= :salary
              AND LOWER(e.name) LIKE LOWER(CONCAT('%', :employeeName, '%'))
            """, nativeQuery = true)
    List<Employee> searchEmployees(
            @Param("departmentName") String departmentName,
            @Param("salary") BigDecimal salary,
            @Param("employeeName") String employeeName);

    @Query(value = """
            SELECT e.* FROM employee e
            JOIN department d ON e.department_id = d.id
            WHERE d.name = ?1
              AND e.salary >= ?2
              AND LOWER(e.name) LIKE LOWER(CONCAT('%', ?3, '%'))
            """, nativeQuery = true)
    List<Employee> searchEmployeesPosition(String departmentName, BigDecimal salary, String employeeName);

    @Query(value = """
            SELECT e.* FROM employee e
            JOIN department d ON e.department_id = d.id
            WHERE d.name ILIKE :departmentName
              AND e.name ILIKE CONCAT('%', :employeeName, '%')
            """, nativeQuery = true)
    List<Employee> searchEmployeesUsingIlike(
            @Param("departmentName") String departmentName,
            @Param("employeeName") String employeeName);
}
