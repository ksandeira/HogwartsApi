package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student updateStudent(Long id, Student studentDetails) {
        Student student = getStudentById(id);
        if (student == null) {
            return null;
        }

        if (studentDetails.getName() != null) {
            student.setName(studentDetails.getName());
        }
        if (studentDetails.getAge() > 0) { // Предполагаем, что возраст всегда > 0
            student.setAge(studentDetails.getAge());
        }
        if (studentDetails.getFaculty() != null) {
            student.setFaculty(studentDetails.getFaculty());
        }

        return studentRepository.save(student);
    }

    public Student deleteStudent(Long id) {
        Student student = getStudentById(id);
        if (student != null) {
            studentRepository.deleteById(id);
        }
        return student;
    }

    public List<Student> getStudentsByAge(int age) {
        return studentRepository.findByAge(age);
    }

    public List<Student> getStudentsByAgeBetween(int minAge, int maxAge) {
        return studentRepository.findByAgeBetween(minAge, maxAge);
    }

    public List<Student> getStudentsByName(String name) {
        return studentRepository.findByNameContainingIgnoreCase(name);
    }

    public Faculty getStudentFaculty(Long studentId) {
        Student student = getStudentById(studentId);
        if (student == null) {
            return null;
        }
        return student.getFaculty();
    }

    public Student assignFacultyToStudent(Long studentId, Faculty faculty) {
        Student student = getStudentById(studentId);
        if (student == null) {
            return null;
        }
        student.setFaculty(faculty);
        return studentRepository.save(student);
    }

    public List<Student> getStudentsWithoutFaculty() {
        return studentRepository.findAll().stream()
                .filter(student -> student.getFaculty() == null)
                .collect(Collectors.toList());
    }

    public List<Student> getStudentsByFacultyId(Long facultyId) {
        return studentRepository.findAll().stream()
                .filter(student -> student.getFaculty() != null &&
                        student.getFaculty().getId().equals(facultyId))
                .collect(Collectors.toList());
    }
}