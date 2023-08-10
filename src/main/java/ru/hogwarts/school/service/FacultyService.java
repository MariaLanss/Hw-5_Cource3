package ru.hogwarts.school.service;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.Collections;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }
    public Collection<Faculty> allFaculties(){
        return facultyRepository.findAll();
    }
    public Collection<Faculty> getFacultiesByColor(String color){
        return facultyRepository.findByColor(color);
    }

    public Collection<Faculty> getFacultyByNameOrColor(String nameOrColor){
        return facultyRepository.findAllByNameIgnoreCaseOrColorIgnoreCase(nameOrColor, nameOrColor);
    }

    public Faculty add(Faculty faculty){
        return facultyRepository.save(faculty);
    }
    public Faculty get(Long id) {
        return facultyRepository.findById(id).orElse(null);
    }

    public Collection<Student> getStudents(Long facultyId){
        return facultyRepository.findById(facultyId).map(Faculty::getStudents)
                .orElseGet(Collections::emptyList);
    }

    public Faculty edit (Faculty faculty) {
        if (get(faculty.getId()) == null){
            return null;
        }
        return facultyRepository.save(faculty);
    }
    public void delete(Long id){
        facultyRepository.deleteById(id);
    }

}