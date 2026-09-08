-- Connect to companydb before running this script.
CREATE TABLE IF NOT EXISTS department (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS employee (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    salary NUMERIC(12, 2),
    department_id BIGINT,
    CONSTRAINT fk_employee_department
        FOREIGN KEY (department_id) REFERENCES department(id)
);
