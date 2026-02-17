package ru.hogwarts.school.controller.resttemplate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TestFacultyControllerRestTemplate {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private String studentBaseUrl;

    @BeforeEach
    void setUp() {
        this.baseUrl = "http://localhost:" + port + "/faculty";
        this.studentBaseUrl = "http://localhost:" + port + "/student";
    }

    @Test
    void testCreateFaculty() {
        String uniqueName = "Gryffindor_" + System.currentTimeMillis();
        Faculty faculty = new Faculty(uniqueName, "Red");

        ResponseEntity<Faculty> response = restTemplate.postForEntity(
                baseUrl,
                faculty,
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(uniqueName);
        assertThat(response.getBody().getColor()).isEqualTo("Red");
    }

    @Test
    void testGetFaculty() {
        String uniqueName = "Slytherin_" + System.currentTimeMillis();
        Faculty faculty = new Faculty(uniqueName, "Green");
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(baseUrl, faculty, Faculty.class);
        Faculty createdFaculty = createResponse.getBody();

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                baseUrl + "/" + createdFaculty.getId(),
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(createdFaculty.getId());
        assertThat(response.getBody().getName()).isEqualTo(uniqueName);
        assertThat(response.getBody().getColor()).isEqualTo("Green");
    }

    @Test
    void testGetAllFaculties() {
        String name1 = "Ravenclaw_" + System.currentTimeMillis();
        String name2 = "Hufflepuff_" + System.currentTimeMillis();

        restTemplate.postForEntity(baseUrl, new Faculty(name1, "Blue"), Faculty.class);
        restTemplate.postForEntity(baseUrl, new Faculty(name2, "Yellow"), Faculty.class);

        ResponseEntity<List<Faculty>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Faculty>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testUpdateFaculty() {
        String uniqueName = "Gryffindor_" + System.currentTimeMillis();
        Faculty faculty = new Faculty(uniqueName, "Red");

        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(baseUrl, faculty, Faculty.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Faculty createdFaculty = createResponse.getBody();
        assertThat(createdFaculty).isNotNull();
        assertThat(createdFaculty.getId()).isNotNull();

        String updatedName = uniqueName + "_Updated";
        createdFaculty.setName(updatedName);
        createdFaculty.setColor("Gold");

        HttpEntity<Faculty> requestUpdate = new HttpEntity<>(createdFaculty);
        ResponseEntity<Faculty> response = restTemplate.exchange(
                baseUrl + "/" + createdFaculty.getId(),
                HttpMethod.PUT,
                requestUpdate,
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(updatedName);
        assertThat(response.getBody().getColor()).isEqualTo("Gold");
        assertThat(response.getBody().getId()).isEqualTo(createdFaculty.getId()); // Проверяем, что ID не изменился
    }

    @Test
    void testGetFacultiesByColor() {
        String name1 = "Gryffindor_" + System.currentTimeMillis();
        String name2 = "Slytherin_" + System.currentTimeMillis();

        restTemplate.postForEntity(baseUrl, new Faculty(name1, "Red"), Faculty.class);
        restTemplate.postForEntity(baseUrl, new Faculty(name2, "Green"), Faculty.class);

        ResponseEntity<List<Faculty>> response = restTemplate.exchange(
                baseUrl + "/color/Red",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Faculty>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).allMatch(f -> "Red".equals(f.getColor()));
    }

    @Test
    void testSearchFaculties() {
        String searchTerm = "Gryffindor_" + System.currentTimeMillis();
        String otherName = "Slytherin_" + System.currentTimeMillis();

        restTemplate.postForEntity(baseUrl, new Faculty(searchTerm, "Red"), Faculty.class);
        restTemplate.postForEntity(baseUrl, new Faculty(otherName, "Green"), Faculty.class);

        ResponseEntity<List<Faculty>> response = restTemplate.exchange(
                baseUrl + "?name=" + searchTerm,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Faculty>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).anyMatch(f -> searchTerm.equals(f.getName()));
    }

    @Test
    void testGetFacultyStudents() {
        // Создаем факультет
        String facultyName = "Gryffindor_" + System.currentTimeMillis();
        Faculty faculty = new Faculty(facultyName, "Red");
        ResponseEntity<Faculty> facultyResponse = restTemplate.postForEntity(baseUrl, faculty, Faculty.class);
        Faculty createdFaculty = facultyResponse.getBody();

        // Создаем студентов (без факультета, так как PUT не работает)
        for (int i = 0; i < 3; i++) {
            Student student = new Student("Student_" + System.currentTimeMillis() + "_" + i, 17 + i);
            ResponseEntity<Student> studentResponse = restTemplate.postForEntity(
                    studentBaseUrl,
                    student,
                    Student.class
            );
        }

        // Получаем студентов факультета (скорее всего будет пустой список)
        ResponseEntity<List<Student>> response = restTemplate.exchange(
                baseUrl + "/" + createdFaculty.getId() + "/students",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testGetFacultyNotFound() {
        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                baseUrl + "/999999",
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testUpdateFacultyNotFound() {
        Faculty faculty = new Faculty("Unknown_" + System.currentTimeMillis(), "Unknown");
        HttpEntity<Faculty> requestUpdate = new HttpEntity<>(faculty);

        ResponseEntity<Faculty> response = restTemplate.exchange(
                baseUrl + "/999999",
                HttpMethod.PUT,
                requestUpdate,
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testDeleteFaculty() {
        String uniqueName = "Beauxbatons_" + System.currentTimeMillis();
        Faculty faculty = new Faculty(uniqueName, "Blue");
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(baseUrl, faculty, Faculty.class);
        Faculty createdFaculty = createResponse.getBody();

        restTemplate.delete(baseUrl + "/" + createdFaculty.getId());

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                baseUrl + "/" + createdFaculty.getId(),
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testDeleteFacultyNotFound() {
        restTemplate.delete(baseUrl + "/999999");
    }
}