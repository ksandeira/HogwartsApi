package ru.hogwarts.school.controller.webmvctest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
public class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FacultyService facultyService;

    @Test
    void testCreateFaculty() throws Exception {
        Faculty faculty = new Faculty("Gryffindor", "Red");
        Faculty savedFaculty = new Faculty("Gryffindor", "Red");
        savedFaculty.setId(1L);

        when(facultyService.createFaculty(any(Faculty.class))).thenReturn(savedFaculty);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("Red"));
    }

    @Test
    void testGetFaculty() throws Exception {
        Faculty faculty = new Faculty("Slytherin", "Green");
        faculty.setId(1L);

        when(facultyService.getFacultyById(1L)).thenReturn(faculty);

        mockMvc.perform(get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Slytherin"))
                .andExpect(jsonPath("$.color").value("Green"));
    }

    @Test
    void testGetFacultyNotFound() throws Exception {
        when(facultyService.getFacultyById(999L)).thenReturn(null);

        mockMvc.perform(get("/faculty/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllFaculties() throws Exception {
        Faculty faculty1 = new Faculty("Gryffindor", "Red");
        faculty1.setId(1L);
        Faculty faculty2 = new Faculty("Slytherin", "Green");
        faculty2.setId(2L);
        Faculty faculty3 = new Faculty("Ravenclaw", "Blue");
        faculty3.setId(3L);
        Faculty faculty4 = new Faculty("Hufflepuff", "Yellow");
        faculty4.setId(4L);

        List<Faculty> faculties = Arrays.asList(faculty1, faculty2, faculty3, faculty4);

        when(facultyService.getAllFaculties()).thenReturn(faculties);

        mockMvc.perform(get("/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Slytherin"));
    }

    @Test
    void testUpdateFaculty() throws Exception {
        Faculty facultyToUpdate = new Faculty("Gryffindor (updated)", "Gold");
        Faculty updatedFaculty = new Faculty("Gryffindor (updated)", "Gold");
        updatedFaculty.setId(1L);

        when(facultyService.updateFaculty(eq(1L), any(Faculty.class))).thenReturn(updatedFaculty);

        mockMvc.perform(put("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(facultyToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Gryffindor (updated)"))
                .andExpect(jsonPath("$.color").value("Gold"));
    }

    @Test
    void testUpdateFacultyNotFound() throws Exception {
        Faculty facultyToUpdate = new Faculty("Unknown Faculty", "Unknown");

        when(facultyService.updateFaculty(eq(999L), any(Faculty.class))).thenReturn(null);

        mockMvc.perform(put("/faculty/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(facultyToUpdate)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteFaculty() throws Exception {
        Faculty deletedFaculty = new Faculty("Beauxbatons", "Blue");
        deletedFaculty.setId(1L);

        when(facultyService.deleteFaculty(1L)).thenReturn(deletedFaculty);

        mockMvc.perform(delete("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Beauxbatons"))
                .andExpect(jsonPath("$.color").value("Blue"));
    }

    @Test
    void testDeleteFacultyNotFound() throws Exception {
        when(facultyService.deleteFaculty(999L)).thenReturn(null);

        mockMvc.perform(delete("/faculty/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetFacultiesByColor() throws Exception {
        Faculty faculty1 = new Faculty("Gryffindor", "Red");
        faculty1.setId(1L);
        List<Faculty> redFaculties = Arrays.asList(faculty1);

        when(facultyService.getFacultiesByColor("Red")).thenReturn(redFaculties);

        mockMvc.perform(get("/faculty/color/Red"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(jsonPath("$[0].color").value("Red"));
    }

    @Test
    void testSearchFaculties() throws Exception {
        Faculty faculty1 = new Faculty("Gryffindor", "Red");
        faculty1.setId(1L);
        List<Faculty> searchResults = Arrays.asList(faculty1);

        when(facultyService.getFacultiesByNameOrColor("Gryff")).thenReturn(searchResults);

        mockMvc.perform(get("/faculty/search")
                        .param("search", "Gryff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Gryffindor"));
    }

    @Test
    void testGetFacultyStudents() throws Exception {
        // Создаем студентов БЕЗ установки faculty (чтобы избежать циклических ссылок)
        Student student1 = new Student("Harry Potter", 17);
        student1.setId(1L);
        Student student2 = new Student("Hermione Granger", 17);
        student2.setId(2L);
        Student student3 = new Student("Ron Weasley", 17);
        student3.setId(3L);

        List<Student> students = Arrays.asList(student1, student2, student3);

        when(facultyService.getFacultyStudents(1L)).thenReturn(students);

        mockMvc.perform(get("/faculty/1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Harry Potter"))
                .andExpect(jsonPath("$[0].age").value(17))
                .andExpect(jsonPath("$[1].name").value("Hermione Granger"))
                .andExpect(jsonPath("$[2].name").value("Ron Weasley"));
    }

    @Test
    void testGetFacultyStudentsEmpty() throws Exception {
        when(facultyService.getFacultyStudents(1L)).thenReturn(List.of());

        mockMvc.perform(get("/faculty/1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}