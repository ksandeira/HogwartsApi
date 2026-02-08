SELECT * FROM students
WHERE age BETWEEN 10 AND 20
ORDER BY age;

SELECT name FROM students
ORDER BY name;

SELECT * FROM students
WHERE LOWER(name) LIKE '%о%' OR LOWER(name) LIKE '%o%'
ORDER BY name;

SELECT * FROM students
WHERE age < id
ORDER BY id;

SELECT * FROM students
ORDER BY age ASC;

SELECT * FROM students
ORDER BY age DESC;

SELECT COUNT(*) as total_students FROM students;

SELECT AVG(age) as average_age FROM students;

SELECT
    MIN(age) as min_age,
    MAX(age) as max_age
FROM students;