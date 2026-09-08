package com.example.employeeapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.employeeapp.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
