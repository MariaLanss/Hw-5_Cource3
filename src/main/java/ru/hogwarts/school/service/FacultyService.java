package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.Collections;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final Logger logger = LoggerFactory.getLogger(FacultyService.class);
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }
    public Collection<Faculty> allFaculties(){
        logger.info("Был вызван метод allFaculties");
        return facultyRepository.findAll();
    }
    public Collection<Faculty> getFacultiesByColor(String color){
        logger.info("Был вызван метод getFacultiesByColor");
        return facultyRepository.findByColor(color);
    }

    public Collection<Faculty> getFacultyByNameOrColor(String nameOrColor){
        logger.info("Был вызван метод getFacultiesByNameOrColor");
        return facultyRepository.findAllByNameIgnoreCaseOrColorIgnoreCase(nameOrColor, nameOrColor);
    }

    public Faculty add(Faculty faculty){
        logger.info("Метод to add a faculty был вызван");
        return facultyRepository.save(faculty);
    }
    public Faculty get(Long id) {
        logger.info("Метод to get a faculty by id был вызван");
        Faculty faculty = facultyRepository.findById(id).orElse(null);
        if (faculty == null){
            logger.warn("Факультет с id = " + id + " не найден");
        }
        return faculty;
    }

    public Collection<Student> getStudents(Long facultyId){
        logger.info("Был вызван метод getStudents");
        return facultyRepository.findById(facultyId).map(Faculty::getStudents)
                .orElseGet(Collections::emptyList);
    }

    public Faculty edit (Faculty faculty) {
        logger.info("Метод to edit a faculty был вызван");
        if (get(faculty.getId()) == null){
            logger.warn("Факультет с id = " + faculty.getId() + " не найден");
            return null;
        }
        return facultyRepository.save(faculty);
    }
    public void delete(Long id){
        logger.info("Метод to delete a faculty был вызван");
        facultyRepository.deleteById(id);
    }

}