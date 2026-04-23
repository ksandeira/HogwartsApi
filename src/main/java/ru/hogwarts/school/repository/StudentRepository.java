package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByAge(int age);

    List<Student> findByAgeBetween(int minAge, int maxAge);

    List<Student> findByNameContainingIgnoreCase(String name);

    @Query("SELECT COUNT(s) FROM Student s")
    long getTotalStudentsCount();

    @Query("SELECT AVG(s.age) FROM Student s")
    double getAverageStudentAge();

    @Query("SELECT s FROM Student s ORDER BY s.id DESC LIMIT 5")
    List<Student> getLastFiveStudents();
}
