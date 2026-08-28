package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        logger.info("Was invoked method for create student");
        logger.debug("Creating student with name: {}", student.getName());

        Student savedStudent = studentRepository.save(student);
        logger.debug("Student created with id: {}", savedStudent.getId());
        return savedStudent;
    }

    public Student getStudentById(Long id) {
        logger.info("Was invoked method for get student by id: {}", id);

        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            logger.warn("Student with id = {} not found", id);
        } else {
            logger.debug("Found student: {}", student.getName());
        }
        return student;
    }

    public List<Student> getAllStudents() {
        logger.info("Was invoked method for get all students");
        List<Student> students = studentRepository.findAll();
        logger.debug("Found {} students", students.size());
        return students;
    }

    public Student updateStudent(Long id, Student studentDetails) {
        logger.info("Was invoked method for update student with id: {}", id);

        Student student = getStudentById(id);
        if (student == null) {
            logger.error("Cannot update: student with id = {} not found", id);
            return null;
        }

        if (studentDetails.getName() != null) {
            logger.debug("Updating name from '{}' to '{}'", student.getName(), studentDetails.getName());
            student.setName(studentDetails.getName());
        }
        if (studentDetails.getAge() > 0) {
            logger.debug("Updating age from {} to {}", student.getAge(), studentDetails.getAge());
            student.setAge(studentDetails.getAge());
        }
        if (studentDetails.getFaculty() != null) {
            student.setFaculty(studentDetails.getFaculty());
        }

        Student updatedStudent = studentRepository.save(student);
        logger.info("Student with id = {} successfully updated", id);
        return updatedStudent;
    }

    public Student deleteStudent(Long id) {
        logger.info("Was invoked method for delete student with id: {}", id);

        Student student = getStudentById(id);
        if (student != null) {
            logger.warn("Deleting student with id = {}, name = {}", id, student.getName());
            studentRepository.deleteById(id);
            logger.info("Student with id = {} successfully deleted", id);
        } else {
            logger.error("Cannot delete: student with id = {} not found", id);
        }
        return student;
    }

    public List<Student> getStudentsByAge(int age) {
        logger.info("Was invoked method for get students by age: {}", age);
        List<Student> students = studentRepository.findByAge(age);
        logger.debug("Found {} students with age {}", students.size(), age);
        return students;
    }

    public List<Student> getStudentsByAgeBetween(int minAge, int maxAge) {
        logger.info("Was invoked method for get students by age between {} and {}", minAge, maxAge);
        List<Student> students = studentRepository.findByAgeBetween(minAge, maxAge);
        logger.debug("Found {} students in age range", students.size());
        return students;
    }

    public Faculty getStudentFaculty(Long studentId) {
        logger.info("Was invoked method for get student faculty, student id: {}", studentId);

        Student student = getStudentById(studentId);
        if (student == null) {
            logger.warn("Student with id = {} not found, cannot get faculty", studentId);
            return null;
        }

        Faculty faculty = student.getFaculty();
        if (faculty == null) {
            logger.debug("Student with id = {} has no faculty", studentId);
        } else {
            logger.debug("Student belongs to faculty: {}", faculty.getName());
        }
        return faculty;
    }

    public Student assignFacultyToStudent(Long studentId, Faculty faculty) {
        logger.info("Was invoked method for assign faculty to student, student id: {}, faculty id: {}",
                studentId, faculty != null ? faculty.getId() : "null");

        Student student = getStudentById(studentId);
        if (student == null) {
            logger.error("Cannot assign faculty: student with id = {} not found", studentId);
            return null;
        }

        student.setFaculty(faculty);
        Student updatedStudent = studentRepository.save(student);
        logger.info("Faculty successfully assigned to student with id = {}", studentId);
        return updatedStudent;
    }

    public long getTotalStudentsCount() {
        logger.info("Was invoked method for get total students count");
        long count = studentRepository.getTotalStudentsCount();
        logger.debug("Total students count: {}", count);
        return count;
    }

    public double getAverageStudentAge() {
        logger.info("Was invoked method for get average student age");
        double average = studentRepository.getAverageStudentAge();
        logger.debug("Average student age: {}", average);
        return average;
    }

    public List<Student> getLastFiveStudents() {
        logger.info("Was invoked method for get last five students");
        List<Student> students = studentRepository.getLastFiveStudents();
        logger.debug("Returning {} last students", students.size());
        return students;
    }

    @GetMapping("/names-starting-with-a")
    public List<String> getStudentNamesStartingWithA() {
        List<String> names = studentService.getStudentNamesStartingWithA();
        return ResponseEntity.ok(names);
    }
}