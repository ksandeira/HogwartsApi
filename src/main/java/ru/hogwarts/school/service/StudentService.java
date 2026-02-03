package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

@Service
@Transactional
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        if (student.getId() != null) {
            throw new IllegalArgumentException("Cannot create student with existing ID");
        }
        return studentRepository.save(student);
    }

    public Student getStudentById(long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public Student updateStudent(long id, Student studentDetails) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            return null;
        }

        student.setName(studentDetails.getName());
        student.setAge(studentDetails.getAge());

        return studentRepository.save(student);
    }

    public Student deleteStudent(long id) {
        Student student = getStudentById(id);
        if (student != null) {
            studentRepository.deleteById(id);
        }
        return student;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Student> getStudentsByAge(int age) {
        return studentRepository.findByAge(age);
    }
}