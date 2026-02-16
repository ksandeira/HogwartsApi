package ru.hogwarts.school.controller.webmvctest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @Test
    void testCreateStudent() throws Exception {
        Student student = new Student("Harry Potter", 17);
        Student savedStudent = new Student("Harry Potter", 17);
        savedStudent.setId(1L);

        when(studentService.createStudent(any(Student.class))).thenReturn(savedStudent);

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Harry Potter"))
                .andExpect(jsonPath("$.age").value(17));
    }

    @Test
    void testGetStudent() throws Exception {
        Student student = new Student("Hermione Granger", 17);
        student.setId(1L);

        when(studentService.getStudentById(1L)).thenReturn(student);

        mockMvc.perform(get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Hermione Granger"))
                .andExpect(jsonPath("$.age").value(17));
    }

    @Test
    void testGetStudentNotFound() throws Exception {
        when(studentService.getStudentById(999L)).thenReturn(null);

        mockMvc.perform(get("/student/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllStudents() throws Exception {
        Student student1 = new Student("Ron Weasley", 17);
        student1.setId(1L);
        Student student2 = new Student("Draco Malfoy", 17);
        student2.setId(2L);
        Student student3 = new Student("Cedric Diggory", 19);
        student3.setId(3L);

        List<Student> students = Arrays.asList(student1, student2, student3);

        when(studentService.getAllStudents()).thenReturn(students);

        mockMvc.perform(get("/student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Ron Weasley"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Draco Malfoy"));
    }

    @Test
    void testGetStudentsByAge() throws Exception {
        Student student1 = new Student("Neville Longbottom", 17);
        student1.setId(1L);
        Student student2 = new Student("Seamus Finnigan", 17);
        student2.setId(2L);

        List<Student> students = Arrays.asList(student1, student2);

        when(studentService.getStudentsByAge(17)).thenReturn(students);

        mockMvc.perform(get("/student/age/17"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].age").value(17))
                .andExpect(jsonPath("$[1].age").value(17));
    }

    @Test
    void testGetStudentsByAgeRange() throws Exception {
        Student student1 = new Student("Ginny Weasley", 15);
        student1.setId(1L);
        Student student2 = new Student("Luna Lovegood", 16);
        student2.setId(2L);
        Student student3 = new Student("Cho Chang", 18);
        student3.setId(3L);

        List<Student> students = Arrays.asList(student1, student2, student3);

        when(studentService.getStudentsByAgeBetween(15, 18)).thenReturn(students);

        mockMvc.perform(get("/student/age")
                        .param("min", "15")
                        .param("max", "18"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].age").value(15))
                .andExpect(jsonPath("$[1].age").value(16))
                .andExpect(jsonPath("$[2].age").value(18));
    }

    @Test
    void testUpdateStudent() throws Exception {
        Student studentToUpdate = new Student("Cedric Diggory (updated)", 20);
        Student updatedStudent = new Student("Cedric Diggory (updated)", 20);
        updatedStudent.setId(1L);

        when(studentService.updateStudent(eq(1L), any(Student.class))).thenReturn(updatedStudent);

        mockMvc.perform(put("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Cedric Diggory (updated)"))
                .andExpect(jsonPath("$.age").value(20));
    }

    @Test
    void testUpdateStudentNotFound() throws Exception {
        Student studentToUpdate = new Student("Unknown Student", 25);

        when(studentService.updateStudent(eq(999L), any(Student.class))).thenReturn(null);

        mockMvc.perform(put("/student/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentToUpdate)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteStudent() throws Exception {
        Student deletedStudent = new Student("Peter Pettigrew", 25);
        deletedStudent.setId(1L);

        when(studentService.deleteStudent(1L)).thenReturn(deletedStudent);

        mockMvc.perform(delete("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Peter Pettigrew"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    void testDeleteStudentNotFound() throws Exception {
        when(studentService.deleteStudent(999L)).thenReturn(null);

        mockMvc.perform(delete("/student/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetStudentFaculty() throws Exception {
        Faculty faculty = new Faculty("Gryffindor", "Red");
        faculty.setId(1L);

        when(studentService.getStudentFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(get("/student/1/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("Red"));
    }

    @Test
    void testGetStudentFacultyNotFound() throws Exception {
        when(studentService.getStudentFaculty(999L)).thenReturn(null);

        mockMvc.perform(get("/student/999/faculty"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAssignFacultyToStudent() throws Exception {
        Faculty faculty = new Faculty("Gryffindor", "Red");
        faculty.setId(1L);

        Student updatedStudent = new Student("Ron Weasley", 17);
        updatedStudent.setId(1L);

        when(studentService.assignFacultyToStudent(eq(1L), any(Faculty.class))).thenReturn(updatedStudent);

        mockMvc.perform(put("/student/1/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ron Weasley"));
    }

    @Test
    void testAssignFacultyToStudentNotFound() throws Exception {
        Faculty faculty = new Faculty("Gryffindor", "Red");
        faculty.setId(1L);

        when(studentService.assignFacultyToStudent(eq(999L), any(Faculty.class))).thenReturn(null);

        mockMvc.perform(put("/student/999/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isNotFound());
    }
}