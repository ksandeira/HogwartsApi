package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class FacultyService {
    private final FacultyRepository facultyRepository;

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        logger.info("Was invoked method for create faculty");
        logger.debug("Creating faculty with name: {}, color: {}", faculty.getName(), faculty.getColor());

        if (faculty.getId() != null) {
            logger.warn("Attempting to create faculty with existing ID: {}", faculty.getId());
            throw new IllegalArgumentException("Cannot create faculty with existing ID");
        }

        Faculty savedFaculty = facultyRepository.save(faculty);
        logger.info("Faculty created with id: {}", savedFaculty.getId());
        return savedFaculty;
    }

    public Faculty getFacultyById(Long id) {
        logger.info("Was invoked method for get faculty by id: {}", id);

        Faculty faculty = facultyRepository.findById(id).orElse(null);
        if (faculty == null) {
            logger.warn("Faculty with id = {} not found", id);
        } else {
            logger.debug("Found faculty: {}", faculty.getName());
        }
        return faculty;
    }

    public Faculty updateFaculty(Long id, Faculty facultyDetails) {
        logger.info("Was invoked method for update faculty with id: {}", id);

        Faculty faculty = getFacultyById(id);
        if (faculty == null) {
            logger.error("Cannot update: faculty with id = {} not found", id);
            return null;
        }

        if (facultyDetails.getName() != null) {
            logger.debug("Updating name from '{}' to '{}'", faculty.getName(), facultyDetails.getName());
            faculty.setName(facultyDetails.getName());
        }
        if (facultyDetails.getColor() != null) {
            logger.debug("Updating color from '{}' to '{}'", faculty.getColor(), facultyDetails.getColor());
            faculty.setColor(facultyDetails.getColor());
        }

        Faculty updatedFaculty = facultyRepository.save(faculty);
        logger.info("Faculty with id = {} successfully updated", id);
        return updatedFaculty;
    }

    public Faculty deleteFaculty(Long id) {
        logger.info("Was invoked method for delete faculty with id: {}", id);

        Faculty faculty = getFacultyById(id);
        if (faculty != null) {
            logger.warn("Deleting faculty with id = {}, name = {}", id, faculty.getName());
            facultyRepository.deleteById(id);
            logger.info("Faculty with id = {} successfully deleted", id);
        } else {
            logger.error("Cannot delete: faculty with id = {} not found", id);
        }
        return faculty;
    }

    public List<Faculty> getAllFaculties() {
        logger.info("Was invoked method for get all faculties");
        List<Faculty> faculties = facultyRepository.findAll();
        logger.debug("Found {} faculties", faculties.size());
        return faculties;
    }

    public List<Faculty> getFacultiesByColor(String color) {
        logger.info("Was invoked method for get faculties by color: {}", color);
        List<Faculty> faculties = facultyRepository.findByColor(color);
        logger.debug("Found {} faculties with color {}", faculties.size(), color);
        return faculties;
    }

    public Faculty getFacultyByName(String name) {
        logger.info("Was invoked method for get faculty by name: {}", name);
        Faculty faculty = facultyRepository.findByName(name);
        if (faculty == null) {
            logger.warn("Faculty with name = {} not found", name);
        }
        return faculty;
    }

    public List<Faculty> getFacultiesByNameOrColor(String searchString) {
        logger.info("Was invoked method for get faculties by name or color: {}", searchString);
        List<Faculty> faculties = facultyRepository.findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(
                searchString, searchString);
        logger.debug("Found {} faculties matching search", faculties.size());
        return faculties;
    }

    public List<Student> getFacultyStudents(Long facultyId) {
        logger.info("Was invoked method for get faculty students, faculty id: {}", facultyId);

        Faculty faculty = getFacultyById(facultyId);
        if (faculty == null) {
            logger.warn("Faculty with id = {} not found, cannot get students", facultyId);
            return Collections.emptyList();
        }

        List<Student> students = faculty.getStudents();
        logger.debug("Faculty has {} students", students.size());
        return students;
    }

    public String getLongestFacultyName() {
        logger.info("Was invoked method for get longest faculty name");

        String longestName = facultyRepository.findAll().stream()
                .map(Faculty::getName)
                .filter(Objects::nonNull)
                .max(Comparator.comparingInt(String::length))
                .orElse("");

        logger.debug("Longest faculty name: {}", longestName);
        return longestName;
    }
}