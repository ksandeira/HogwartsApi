package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collections;
import java.util.List;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty getFacultyById(Long id) {
        return facultyRepository.findById(id).orElse(null);
    }

    public List<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }

    public Faculty updateFaculty(Long id, Faculty facultyDetails) {
        Faculty faculty = getFacultyById(id);
        if (faculty == null) {
            return null;
        }

        if (facultyDetails.getName() != null) {
            faculty.setName(facultyDetails.getName());
        }
        if (facultyDetails.getColor() != null) {
            faculty.setColor(facultyDetails.getColor());
        }

        return facultyRepository.save(faculty);
    }

    public Faculty deleteFaculty(Long id) {
        Faculty faculty = getFacultyById(id);
        if (faculty != null) {
            facultyRepository.deleteById(id);
        }
        return faculty;
    }

    public List<Faculty> getFacultiesByColor(String color) {
        return facultyRepository.findByColor(color);
    }

    public Faculty getFacultyByName(String name) {
        return facultyRepository.findByName(name);
    }

    public List<Faculty> getFacultiesByNameOrColor(String searchString) {
        return facultyRepository.findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(
                searchString, searchString);
    }

    public List<Faculty> getFacultiesByNameAndColor(String name, String color) {
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(name, color);
    }

    public List<Student> getFacultyStudents(Long facultyId) {
        Faculty faculty = getFacultyById(facultyId);
        if (faculty == null) {
            return Collections.emptyList();
        }
        return faculty.getStudents();
    }

    public int getFacultyStudentCount(Long facultyId) {
        Faculty faculty = getFacultyById(facultyId);
        if (faculty == null || faculty.getStudents() == null) {
            return 0;
        }
        return faculty.getStudents().size();
    }
}