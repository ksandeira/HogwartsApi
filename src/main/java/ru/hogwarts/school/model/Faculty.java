package ru.hogwarts.school.model;

import com.fasterxml.jackson.annotation.JsonIgnore;  // ← Добавьте импорт, если решите использовать JsonIgnore здесь
import jakarta.persistence.*;
import java.util.ArrayList;  // ← Добавьте для инициализации списка
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "faculties")
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String color;

    @Version
    private Long version = 0L;

    @OneToMany(mappedBy = "faculty", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Student> students = new ArrayList<>();

    public Faculty() {
    }

    public Faculty(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public void addStudent(Student student) {
        students.add(student);
        student.setFaculty(this);
    }

    public void removeStudent(Student student) {
        students.remove(student);
        student.setFaculty(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Faculty faculty = (Faculty) o;
        return Objects.equals(id, faculty.id) &&
                Objects.equals(name, faculty.name) &&
                Objects.equals(color, faculty.color) &&
                Objects.equals(version, faculty.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, version);
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", studentsCount=" + (students != null ? students.size() : 0) +  // ← Полезно для отладки
                '}';
    }
}