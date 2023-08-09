package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
@RestController
@RequestMapping("student")
public class StudentController {
    private final StudentService studentService;
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }
    @GetMapping
    public Collection<Student> getAllStudents(){
        return studentService.getAllStudents();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Student> get(@PathVariable long id){
        Student studentFound = studentService.get(id);
        if (studentFound == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(studentFound);
    }
    @GetMapping(params = "age")
    public Collection<Student> getStudentsByAge(@RequestParam Integer age){
        return studentService.getStudentsByAge(age);
    }

    @GetMapping(params = {"min", "max"})
    public Collection<Student> getStudentsByAgeBetween(@RequestParam Integer min,
                                                       @RequestParam Integer max){
        return studentService.getStudentsAgeBetween(min, max);
    }

    @GetMapping("/{id}/faculty")
    public Faculty getFacultyForStudent(@PathVariable Long id){
        return studentService.getFaculty(id);
    }

    @PostMapping
    public Student add(@RequestBody Student student){
        return studentService.add(student);
    }
    @PutMapping()
    public ResponseEntity<Student> edit(@RequestBody Student student) {
        Student studentFound = studentService.edit(student);
        if (studentFound == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(studentFound);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable long id){
        studentService.delete(id);
        return ResponseEntity.ok().build();
    }
}