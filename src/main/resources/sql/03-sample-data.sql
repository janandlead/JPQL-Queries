-- Connect to companydb before running this script.
INSERT INTO department (name)
VALUES ('IT'), ('HR'), ('Finance')
ON CONFLICT DO NOTHING;

INSERT INTO employee (name, email, salary, department_id)
SELECT data.name, data.email, data.salary, d.id
FROM (VALUES
    ('Anand', 'anand@gmail.com', 75000.00, 'IT'),
    ('Priya', 'priya@gmail.com', 68000.00, 'IT'),
    ('Rahul', 'rahul@gmail.com', 62000.00, 'IT'),
    ('Sneha', 'sneha@gmail.com', 58000.00, 'IT'),
    ('Meera', 'meera@gmail.com', 55000.00, 'HR'),
    ('Arjun', 'arjun@gmail.com', 52000.00, 'HR'),
    ('Kiran', 'kiran@gmail.com', 49000.00, 'HR'),
    ('Vikram', 'vikram@gmail.com', 82000.00, 'Finance'),
    ('Divya', 'divya@gmail.com', 71000.00, 'Finance'),
    ('Neha', 'neha@gmail.com', 64000.00, 'Finance')
) AS data(name, email, salary, department_name)
JOIN department d ON d.name = data.department_name
ON CONFLICT (email) DO NOTHING;
