package ru.hogwarts.school.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;
@RestController
@RequestMapping("faculty")
public class FacultyController {
    private final FacultyService facultyService;
    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }
    @GetMapping
    public Collection<Faculty> getAllFaculties(){
        return facultyService.allFaculties();
    }
    @GetMapping(params = "color")
    public Collection<Faculty> getFacultiesByColor(@RequestParam String color){
        return facultyService.getFacultiesByColor(color);
    }

    @GetMapping(params = "nameOrColor")
    public Collection<Faculty> getFacultiesByNameOrColor(@RequestParam String nameOrColor){
        return facultyService.getFacultyByNameOrColor(nameOrColor);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Faculty> getFaculty(@PathVariable long id){
        Faculty facultyFound = facultyService.get(id);
        if (facultyFound == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(facultyFound);
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<Collection<Student>> getStudentsForFaculty(@PathVariable long id) {
        return ResponseEntity.ok(facultyService.getStudents(id));
    }

    @PostMapping()
    public Faculty addFaculty(@RequestBody Faculty faculty){
        return facultyService.add(faculty);
    }
    @PutMapping()
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty) {
        Faculty facultyFound = facultyService.edit(faculty);
        if (facultyFound == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(facultyFound);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity deleteFaculty(@PathVariable long id){
        facultyService.delete(id);
        return ResponseEntity.ok().build();
    }
}