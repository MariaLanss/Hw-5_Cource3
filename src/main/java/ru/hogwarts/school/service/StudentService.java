package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;


import java.util.Collection;
import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    public Collection<Student> getAllStudents(){
        return studentRepository.findAll();
    }
    public Collection<Student> getStudentsByAge(int age){
        return studentRepository.findByAge(age);
    }

    public Collection<Student> getStudentsAgeBetween(int min, int max){
        return studentRepository.findAllByAgeBetween(min,max);
    }

    public Faculty getFaculty(Long studentId){
        return studentRepository.findById(studentId)
                .map(Student::getFaculty).orElse(null);
    }
    public Integer countAllStudents(){
        return studentRepository.countAllStudents();
    }

    public Student add(Student student){
        return studentRepository.save(student);
    }
    public Student get(Long id){
        return studentRepository.findById(id).orElse(null);
    }
    public Student edit (Student student) {
        if (get(student.getId()) == null){
            return null;
        }
        return studentRepository.save(student);
    }
    public void delete(Long id){
        studentRepository.deleteById(id);
    }
    public Double getAverageAge() {
        return studentRepository.getAverageAge();
    }

    public List<Student> getLast5Students(Integer offset){
        return studentRepository.getLast5Students(offset);
    }
}