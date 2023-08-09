package ru.hogwarts.school;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class FacultyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private FacultyService facultyService;
    @SpyBean
    private AvatarService avatarService;
    @SpyBean
    private StudentService studentService;

    @MockBean
    private FacultyRepository facultyRepository;
    @MockBean
    private StudentRepository studentRepository;
    @MockBean
    private AvatarRepository avatarRepository;

    @InjectMocks
    private FacultyController facultyController;

    private List<Faculty> faculties = List.of(
            new Faculty(1L, "Gryffindor", "red"),
            new Faculty(2L, "Slytherin", "green"),
            new Faculty(3L, "Hufflepuff", "yellow"),
            new Faculty(4L, "Ravenclaw", "blue"));

    @Test
    void shouldReturnAllFaculties() throws Exception {
        when(facultyRepository.findAll()).thenReturn(faculties);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty"))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.*", isA(Collection.class)))
                .andExpect(jsonPath("$.*", hasSize(4)));
    }

    @Test
    void shouldReturnFacultiesByColor() throws Exception {
        final String color = "red";
        final int index = 0;

        when(facultyRepository.findByColor(any(String.class))).thenReturn((Collection<Faculty>) faculties
                .stream().filter(f -> f.getColor().equals(color)).collect(Collectors.toList()));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty?color=" + color))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.*", isA(Collection.class)))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(faculties.get(index).getId()))
                .andExpect(jsonPath("$[0].name").value(faculties.get(index).getName()))
                .andExpect(jsonPath("$[0].color").value(faculties.get(index).getColor()))
        ;
    }

    @Test
    void shouldReturnFacultyByNameOrColor() throws Exception{
        final String nameOrColor = "slytherin";
        final int index = 1;

        when(facultyRepository.findAllByNameIgnoreCaseOrColorIgnoreCase(nameOrColor, nameOrColor)).thenReturn(
                faculties.stream().filter(f-> f.getName().equalsIgnoreCase(nameOrColor) || f.getColor().equalsIgnoreCase(nameOrColor))
                        .collect(Collectors.toList()));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty?nameOrColor=" + nameOrColor))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.*", isA(Collection.class)))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(faculties.get(index).getId()))
                .andExpect(jsonPath("$[0].name").value(faculties.get(index).getName()))
                .andExpect(jsonPath("$[0].color").value(faculties.get(index).getColor()))
        ;
    }

    @Test
    void shouldReturnFacultyById() throws Exception {
        Faculty expected = faculties.get(0);
        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.of(expected));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expected.getId()))
                .andExpect(jsonPath("$.name").value(expected.getName()))
                .andExpect(jsonPath("$.color").value(expected.getColor()));
    }

    @Test
    void shouldReturnStudentsForFaculty() throws Exception{
        final long facultyId = 1L;
        Faculty gryffindorFaculty = faculties.get(0);

        List<Student> gryffindorStudents=List.of(
                new Student(1L, "Harry Potter", 11),
                new Student(2L, "Ron Weasley", 11),
                new Student(3L, "Hermiony Granger", 11)
        );
        gryffindorFaculty.setStudents(gryffindorStudents);
        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.of(gryffindorFaculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + facultyId +"/students"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.*", isA(Collection.class)))
                .andExpect(jsonPath("$.*", hasSize(3)));
    }

    @Test
    void shouldReturntFacutlyWhennAddCalled() throws Exception{
        Faculty faculty = new Faculty(5L, "Some faculty", "black");
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        JSONObject facultyObject = new JSONObject();
        facultyObject.put("id", faculty.getId());
        facultyObject.put("name", faculty.getName());
        facultyObject.put("color", faculty.getColor());

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculty.getId()))
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
    }

    @Test
    void shouldReturnFacultyWhenEditCalled() throws Exception{
        Faculty faculty = faculties.get(3);
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);
        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.of(faculty));

        JSONObject facultyObject = new JSONObject();
        facultyObject.put("id", faculty.getId());
        facultyObject.put("name", faculty.getName());
        facultyObject.put("color", faculty.getColor());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculty.getId()))
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()))
        ;
    }

    @Test
    void shouldReturnOKWhenDeleteCalled() throws Exception{
        doNothing().when(facultyRepository).deleteById(any(Long.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/faculty/4"))
                .andExpect(status().isOk());

        verify(facultyRepository, only()).deleteById(any(Long.class));
    }

}