package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;


@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;

    private final Object printLock = new Object();

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Student createdStudent = studentService.createStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
    }

    @GetMapping("/age")
    public ResponseEntity<List<Student>> getStudentsByAgeRange(
            @RequestParam int min,
            @RequestParam int max) {
        List<Student> students = studentService.getStudentsByAgeBetween(min, max);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/age/{age}")
    public ResponseEntity<List<Student>> getStudentsByAge(@PathVariable int age) {
        List<Student> students = studentService.getStudentsByAge(age);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable long id) {
        Student student = studentService.getStudentById(id);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(student);
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable long id,
            @RequestBody Student student) {
        Student updatedStudent = studentService.updateStudent(id, student);
        if (updatedStudent == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Student> deleteStudent(@PathVariable long id) {
        Student deletedStudent = studentService.deleteStudent(id);
        if (deletedStudent == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(deletedStudent);
    }

    @GetMapping("/{id}/faculty")
    public ResponseEntity<Faculty> getStudentFaculty(@PathVariable long id) {
        Faculty faculty = studentService.getStudentFaculty(id);
        if (faculty == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(faculty);
    }

    @PutMapping("/{studentId}/faculty/{facultyId}")
    public ResponseEntity<Student> assignFacultyToStudent(
            @PathVariable long studentId,
            @PathVariable long facultyId,
            @RequestBody Faculty faculty) {
        Student student = studentService.assignFacultyToStudent(studentId, faculty);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(student);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalStudentsCount() {
        long count = studentService.getTotalStudentsCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/average-age")
    public ResponseEntity<Double> getAverageStudentAge() {
        double averageAge = studentService.getAverageStudentAge();
        return ResponseEntity.ok(averageAge);
    }

    @GetMapping("/last-five")
    public ResponseEntity<List<Student>> getLastFiveStudents() {
        List<Student> students = studentService.getLastFiveStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/names-starting-with-a")
    public ResponseEntity<List<String>> getStudentNamesStartingWithA() {
        List<String> names = studentService.getStudentNamesStartingWithA();
        return ResponseEntity.ok(names);
    }

    @GetMapping("/print-parallel")
    public void printStudentsParallel() {
        List<Student> students = studentService.getAllStudents();

        if (students.size() < 6) {
            System.out.println("Недостаточно студентов. Нужно минимум 6, найдено: " + students.size());
            return;
        }

        System.out.println("Имя студента 1: " + students.get(0).getName());
        System.out.println("Имя студента 2: " + students.get(1).getName());

        Thread thread1 = new Thread(() -> {
            System.out.println("Имя студента 3: " + students.get(2).getName());
            System.out.println("Имя студента 4: " + students.get(3).getName());
        });

        Thread thread2 = new Thread(() -> {
            System.out.println("Имя студента 5: " + students.get(4).getName());
            System.out.println("Имя студента 6: " + students.get(5).getName());
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/print-synchronized")
    public void printStudentsSynchronized() {
        List<Student> students = studentService.getAllStudents();

        if (students.size() < 6) {
            System.out.println("Недостаточно студентов. Нужно минимум 6, найдено: " + students.size());
            return;
        }

        printStudentName(students.get(0));
        printStudentName(students.get(1));

        Thread thread1 = new Thread(() -> {
            printStudentName(students.get(2));
            printStudentName(students.get(3));
        });

        Thread thread2 = new Thread(() -> {
            printStudentName(students.get(4));
            printStudentName(students.get(5));
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void printStudentName(Student student) {
        System.out.println("Имя студента: " + student.getName());
    }
}