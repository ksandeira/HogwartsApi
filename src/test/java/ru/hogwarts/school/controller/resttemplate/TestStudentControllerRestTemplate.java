package ru.hogwarts.school.controller.resttemplate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.model.Student;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class TestStudentControllerRestTemplate {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private String facultyBaseUrl;

    @BeforeEach
    void setUp() {
        this.baseUrl = "http://localhost:" + port + "/student";
        this.facultyBaseUrl = "http://localhost:" + port + "/faculty";
    }

    @Test
    void testCreateStudent() {
        Student student = new Student("Harry Potter", 17);

        ResponseEntity<Student> response = restTemplate.postForEntity(
                baseUrl,
                student,
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Harry Potter");
        assertThat(response.getBody().getAge()).isEqualTo(17);
    }

    @Test
    void testGetStudent() {
        Student student = new Student("Hermione Granger", 17);
        ResponseEntity<Student> createResponse = restTemplate.postForEntity(baseUrl, student, Student.class);
        Student createdStudent = createResponse.getBody();

        ResponseEntity<Student> response = restTemplate.getForEntity(
                baseUrl + "/" + createdStudent.getId(),
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(createdStudent.getId());
        assertThat(response.getBody().getName()).isEqualTo("Hermione Granger");
        assertThat(response.getBody().getAge()).isEqualTo(17);
    }

    @Test
    void testGetStudentNotFound() {
        ResponseEntity<Student> response = restTemplate.getForEntity(
                baseUrl + "/999",
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetAllStudents() {
        restTemplate.postForEntity(baseUrl, new Student("Ron Weasley", 17), Student.class);
        restTemplate.postForEntity(baseUrl, new Student("Draco Malfoy", 17), Student.class);

        ResponseEntity<List<Student>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testGetStudentsByAge() {
        restTemplate.postForEntity(baseUrl, new Student("Neville Longbottom", 17), Student.class);
        restTemplate.postForEntity(baseUrl, new Student("Luna Lovegood", 16), Student.class);

        ResponseEntity<List<Student>> response = restTemplate.exchange(
                baseUrl + "/age/17",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).allMatch(s -> s.getAge() == 17);
    }

    @Test
    void testGetStudentsByAgeRange() {
        restTemplate.postForEntity(baseUrl, new Student("Ginny Weasley", 15), Student.class);
        restTemplate.postForEntity(baseUrl, new Student("Luna Lovegood", 16), Student.class);
        restTemplate.postForEntity(baseUrl, new Student("Cho Chang", 18), Student.class);

        ResponseEntity<List<Student>> response = restTemplate.exchange(
                baseUrl + "/age?min=15&max=17",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}