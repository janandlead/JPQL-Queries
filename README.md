# Employee-Department Spring Boot + PostgreSQL

Java 21 / Spring Boot 3.4 / Maven / Spring Data JPA / PostgreSQL / Lombok / Bean Validation / OpenAPI.

## Architecture design

The application follows a layered architecture:

```text
Client / Postman / Swagger
          |
          v
EmployeeController  - HTTP routes, request parameters, status codes
          |
          v
EmployeeService     - business rules and entity-to-DTO mapping
          |
          v
EmployeeRepository  - Spring Data JPA and native PostgreSQL queries
DepartmentRepository
          |
          v
PostgreSQL           - department and employee tables
```

### Layer responsibilities

| Layer | Responsibility |
|---|---|
| Controller | Defines REST endpoints, reads path/query parameters, validates request DTOs, and returns response DTOs. |
| Service | Checks duplicate emails and department existence, calls repositories, handles business exceptions, and maps entities to DTOs. |
| Repository | Extends `JpaRepository` and contains database queries using named and positional native parameters. |
| Entity | Represents the `department` and `employee` database tables. |
| DTO | Defines the public API contract without exposing JPA entities. |
| Exception | Provides domain-specific errors and consistent HTTP error responses. |
| PostgreSQL | Stores departments and employees and executes PostgreSQL SQL, including `ILIKE`. |

### Request flow

1. The client sends an HTTP request to `EmployeeController`.
2. Bean Validation checks `EmployeeRequestDto` for blank names, invalid email, invalid salary, and missing department ID.
3. `EmployeeServiceImpl` checks whether the email is already used and whether the department exists.
4. The service calls `EmployeeRepository` or `DepartmentRepository`.
5. Spring Data JPA executes the query against PostgreSQL.
6. The service maps the result to `EmployeeResponseDto`.
7. The controller returns the DTO as JSON.
8. `GlobalExceptionHandler` converts business and validation errors into structured JSON.

### Entity relationship

```text
department
----------
id PK
name
   1
   |
   | referenced by employee.department_id
   |
   many
employee
--------
id PK
name
email UNIQUE
salary NUMERIC(12,2)
department_id FK
```

`Employee.department` uses `@ManyToOne(fetch = FetchType.LAZY)` and `@JoinColumn(name = "department_id")`. The REST layer returns department ID and name through a DTO instead of exposing the entity graph.

## Run it

1. Start PostgreSQL and run `src/main/resources/sql/01-create-database.sql` while connected to the default `postgres` database.
2. Connect to `companydb`, then run `02-create-tables.sql` and `03-sample-data.sql`.
3. Update `spring.datasource.username` and `spring.datasource.password` in `src/main/resources/application.properties`.
4. Run `mvn spring-boot:run`.
5. Open Swagger at `http://localhost:8080/swagger-ui/index.html`.

`ddl-auto=update` is convenient for learning, but the supplied SQL scripts are the source of truth for the PostgreSQL schema.

## Query guide

`@Query` supplies a custom repository query. With `nativeQuery = true`, the query is actual PostgreSQL SQL and uses table/column names such as `employee` and `department_id`.

Named parameters use `:email`, bound by `@Param("email") String email`. The `@Param` name must match the placeholder exactly. Positional parameters use `?1`, `?2`, and `?3`; each number refers to the method parameter at that position.

JPQL works with entity names and Java properties:

```java
@Query("""
       SELECT e FROM Employee e WHERE e.email = :email
       """)
```

Native SQL works with database identifiers and PostgreSQL syntax:

```java
@Query(value = "SELECT * FROM employee WHERE email = :email", nativeQuery = true)
```

| Dimension | Named | Positional |
|---|---|---|
| Syntax | `:email` | `?1` |
| Readability | High | Lower in larger queries |
| Maintainability | Better | Parameter order must stay correct |
| Recommended | Medium/complex queries | Simple queries or demonstrations |

## Postman API testing

Start the application with `mvn spring-boot:run`, then use this base URL in Postman:

```text
http://localhost:8080/api/v1/employees
```

For the POST request, select `Body > raw > JSON` and add the `Content-Type: application/json` header. GET requests do not need a request body.

### 1. Add employee

```text
POST http://localhost:8080/api/v1/employees
```

```json
{
  "name": "Anand",
  "email": "anand@gmail.com",
  "salary": 75000.00,
  "departmentId": 1
}
```

Expected result: `201 Created`.

### 2. Find employee by email - named parameter

```text
GET http://localhost:8080/api/v1/employees/email?email=anand@gmail.com
```

Expected result: `200 OK` with one employee. Unknown email returns `404 Not Found`.

### 3. Find employee by email - positional parameter

```text
GET http://localhost:8080/api/v1/employees/email/position?email=anand@gmail.com
```

Expected result: `200 OK` with one employee. Unknown email returns `404 Not Found`.

### 4. Find by department ID and minimum salary - named parameters

```text
GET http://localhost:8080/api/v1/employees/department/1?minSalary=60000
```

Expected result: `200 OK` with IT employees whose salary is at least `60000`. An unknown department ID returns `404 Not Found`.

### 5. Find by department ID and minimum salary - positional parameters

```text
GET http://localhost:8080/api/v1/employees/department/1/position?minSalary=60000
```

Expected result: `200 OK` with a matching employee list. An unknown department ID returns `404 Not Found`.

### 6. Find by department name - named parameter

```text
GET http://localhost:8080/api/v1/employees/department-name/IT
```

Expected result: `200 OK` with all IT employees. An unknown department name returns `200 OK` with an empty list.

### 7. Find by department name - positional parameter

```text
GET http://localhost:8080/api/v1/employees/department-name/IT/position
```

Expected result: `200 OK` with all IT employees.

### 8. Complex search

```text
GET http://localhost:8080/api/v1/employees/search?departmentName=IT&salary=60000&employeeName=An
```

Expected result: `200 OK` with employees in IT earning at least `60000` whose name contains `An`.

### 9. PostgreSQL ILIKE search

```text
GET http://localhost:8080/api/v1/employees/search-ilike?departmentName=it&employeeName=an
```

Expected result: `200 OK`. This matches department and employee names without case sensitivity because it uses PostgreSQL `ILIKE`.

### Successful response example

```json
{
  "id": 1,
  "name": "Anand",
  "email": "anand@gmail.com",
  "salary": 75000.00,
  "departmentId": 1,
  "departmentName": "IT"
}
```

### Negative request examples

Duplicate email:

```text
POST http://localhost:8080/api/v1/employees
```

```json
{
  "name": "Another Anand",
  "email": "anand@gmail.com",
  "salary": 70000,
  "departmentId": 1
}
```

Expected result: `409 Conflict`.

Invalid email, blank name, negative salary, and missing department ID:

```json
{
  "name": "",
  "email": "not-an-email",
  "salary": -10,
  "departmentId": null
}
```

Expected result: `400 Bad Request` with a `fieldErrors` object containing the validation messages. A nonexistent `departmentId` returns `404 Not Found`.

## Direct PostgreSQL tests

Run these in pgAdmin Query Tool or `psql` after connecting to `companydb`:

```sql
SELECT * FROM department;
SELECT * FROM employee;
SELECT * FROM employee WHERE email = 'anand@gmail.com';
SELECT * FROM employee WHERE department_id = 1 AND salary >= 60000;
SELECT e.* FROM employee e JOIN department d ON e.department_id = d.id WHERE d.name = 'IT';
SELECT e.* FROM employee e JOIN department d ON e.department_id = d.id
WHERE d.name ILIKE 'it' AND e.name ILIKE '%an%';
```

## PostgreSQL notes

- `BIGSERIAL` creates a `BIGINT` column backed by an auto-incrementing sequence.
- `NUMERIC(12,2)` stores exact decimal values and maps naturally to Java `BigDecimal`.
- `ILIKE` is PostgreSQL-specific case-insensitive matching.
- `RETURNING` can return inserted rows, for example `INSERT INTO department(name) VALUES ('Legal') RETURNING id, name;`.
- `LIMIT` and `OFFSET` support simple paging: `SELECT * FROM employee ORDER BY id LIMIT 5 OFFSET 0;`.

## Interview questions

1. What does `@Query` do in Spring Data JPA?
2. What does `nativeQuery = true` change?
3. What is a named parameter and what does `@Param` do?
4. What does `?1` mean? What do `?2` and `?3` mean?
5. When are named parameters preferable to positional parameters?
6. How do JPQL and native SQL differ?
7. When should a native query be used?
8. Why is `BigDecimal` preferred over `Double` for salary?
9. What is PostgreSQL `ILIKE`, and how does it differ from `LIKE`?
10. Can named and positional parameters be used in JPQL? Yes, both are supported.
11. Why is native SQL less portable than JPQL?
12. What can happen if `?1` and `?2` are mapped incorrectly?
13. Why should entities not be returned directly from REST controllers?

## Structure

```text
src/main/java/com/example/employeeapp
|-- controller/EmployeeController.java
|-- service/EmployeeService.java
|-- service/impl/EmployeeServiceImpl.java
|-- repository/{EmployeeRepository,DepartmentRepository}.java
|-- entity/{Employee,Department}.java
|-- dto/{EmployeeRequestDto,EmployeeResponseDto}.java
|-- exception/{...}.java
`-- EmployeeDepartmentApplication.java
```
