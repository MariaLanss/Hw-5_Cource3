package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

        Collection<Student> findByAge(int age);

        Collection<Student> findAllByAgeBetween(int min, int max);

        @Query(value = "SELECT COUNT(*) FROM student", nativeQuery = true)
        Integer countAllStudents();

        @Query(value = "SELECT AVG(age) FROM student", nativeQuery = true)
        Double getAverageAge();

        @Query(value = "SELECT * FROM student ORDER BY id OFFSET :offset", nativeQuery = true)
        List<Student> getLast5Students (Integer offset);

}