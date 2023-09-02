package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;



import java.util.Collection;
import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final Logger logger = LoggerFactory.getLogger(StudentService.class);
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    public Collection<Student> getAllStudents(){
        logger.info("Был вызван метод getAllStudents");
        return studentRepository.findAll();
    }
    public Collection<Student> getStudentsByAge(int age){
        logger.info("Был вызван метод getStudentsByAge");
        return studentRepository.findByAge(age);
    }

    public Collection<Student> getStudentsAgeBetween(int min, int max){
        logger.info("Был вызван метод getStudentsAgeBetween");
        return studentRepository.findAllByAgeBetween(min,max);
    }

    public Faculty getFaculty(Long studentId){
        logger.info("Был вызван метод getFaculty");
        return studentRepository.findById(studentId)
                .map(Student::getFaculty).orElse(null);
    }
    public Integer countAllStudents(){
        logger.info("Был вызван метод countAllStudents");
        return studentRepository.countAllStudents();
    }

    public Student add(Student student){
        logger.info("Метод for adding a student был вызван");
        return studentRepository.save(student);
    }
    public Student get(Long id){
        logger.info("Метод for getting a student был вызван");
        return studentRepository.findById(id).orElse(null);
    }
    public Student edit (Student student) {
        logger.info("Метод for editing a student был вызван");
        if (get(student.getId()) == null){
            logger.warn("Студент с id = " + student.getId() + " не найден");
            return null;
        }
        return studentRepository.save(student);
    }
    public void delete(Long id){
        logger.info("Метод for student deletion был вызван");
        if (get(id) == null) {
            logger.warn("Студент с id = " + id + " не найден");
        }
        studentRepository.deleteById(id);
    }
    public Double getAverageAge() {
        logger.info("Был вызван метод getAverageAge");
        return studentRepository.getAverageAge();
    }

    public List<Student> getLast5Students(Integer offset){
        logger.info("Был вызван метод getLast5Students");
        int studentsQty = countAllStudents();
        if (studentsQty < 5){
            logger.warn("Only " + studentsQty + " exist");
        }
        return studentRepository.getLast5Students(offset);
    }
}