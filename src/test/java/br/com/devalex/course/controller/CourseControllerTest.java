package br.com.devalex.course.controller;


import br.com.devalex.course.exceptions.ErrorMessages;
import br.com.devalex.course.exceptions.custom.ResourceNotFoundException;
import br.com.devalex.course.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static br.com.devalex.course.common.TestFixtures.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CourseController.class)
@DisplayName("CourseController")
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CourseService courseService;

    @Nested
    @DisplayName("POST /courses")
    class Save {

        @Test
        @DisplayName("should return 201 and the created course when request is valid")
        void shouldReturn201WhenRequestIsValid() throws Exception {
            when(courseService.save(any())).thenReturn(courseResponse());

            mockMvc.perform(post("/courses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(courseRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(COURSE_ID.toString()))
                    .andExpect(jsonPath("$.name").value("Spring Boot na Prática"));
        }

        @Test
        @DisplayName("should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            String body = """
                {
                  "name": "",
                  "imgUrl": "https://img.example.com/spring.jpg",
                  "courseStatus": "INPROGRESS",
                  "courseLevel": "BEGINNER",
                  "userInstructor": "44444444-4444-4444-4444-444444444444",
                  "description": "Descrição válida"
                }
                """;

            mockMvc.perform(post("/courses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.name").exists());
        }

        @Test
        @DisplayName("should return 400 when imgUrl has invalid format")
        void shouldReturn400WhenImgUrlIsInvalid() throws Exception {
            String body = """
                {
                  "name": "Spring Boot",
                  "imgUrl": "not-a-url",
                  "courseStatus": "INPROGRESS",
                  "courseLevel": "BEGINNER",
                  "userInstructor": "44444444-4444-4444-4444-444444444444",
                  "description": "Descrição válida"
                }
                """;

            mockMvc.perform(post("/courses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.imgUrl").exists());
        }

        @Test
        @DisplayName("should return 400 when required fields are missing")
        void shouldReturn400WhenRequiredFieldsAreMissing() throws Exception {
            mockMvc.perform(post("/courses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", aMapWithSize(greaterThan(0))));

        }
    }

    @Nested
    @DisplayName("GET /courses")
    class FindAll {

        @Test
        @DisplayName("should return 200 and a list of courses")
        void shouldReturn200WithListOfCourses() throws Exception {
            when(courseService.findAll()).thenReturn(List.of(courseResponse()));

            mockMvc.perform(get("/courses"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(COURSE_ID.toString()));
        }

        @Test
        @DisplayName("should return 200 and an empty list when there are no courses")
        void shouldReturn200WithEmptyList() throws Exception {
            when(courseService.findAll()).thenReturn(List.of());

            mockMvc.perform(get("/courses"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /courses/{id}")
    class FindById {

        @Test
        @DisplayName("should return 200 and the course when it exists")
        void shouldReturn200WhenCourseExists() throws Exception {
            when(courseService.findById(COURSE_ID)).thenReturn(courseResponse());

            mockMvc.perform(get("/courses/{id}", COURSE_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(COURSE_ID.toString()))
                    .andExpect(jsonPath("$.name").value("Spring Boot na Prática"));
        }

        @Test
        @DisplayName("should return 404 when course does not exist")
        void shouldReturn404WhenCourseDoesNotExist() throws Exception {
            UUID id = UUID.randomUUID();
            when(courseService.findById(id))
                    .thenThrow(new ResourceNotFoundException(String.format(ErrorMessages.COURSE_NOT_FOUND, id)));

            mockMvc.perform(get("/courses/{id}", id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("should return 400 when id is not a valid UUID")
        void shouldReturn400WhenIdIsNotUUID() throws Exception {
            mockMvc.perform(get("/courses/{id}", "not-a-uuid"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /courses/{id}")
    class Update {

        @Test
        @DisplayName("should return 200 and the updated course when request is valid")
        void shouldReturn200WhenUpdateIsSuccessful() throws Exception {
            when(courseService.update(eq(COURSE_ID), any())).thenReturn(courseResponse());

            mockMvc.perform(put("/courses/{id}", COURSE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(courseRequest())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(COURSE_ID.toString()));
        }

        @Test
        @DisplayName("should return 404 when course does not exist")
        void shouldReturn404WhenCourseDoesNotExist() throws Exception {
            UUID id = UUID.randomUUID();
            when(courseService.update(eq(id), any()))
                    .thenThrow(new ResourceNotFoundException(String.format(ErrorMessages.COURSE_NOT_FOUND, id)));

            mockMvc.perform(put("/courses/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(courseRequest())))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return 400 when body is invalid")
        void shouldReturn400WhenBodyIsInvalid() throws Exception {
            mockMvc.perform(put("/courses/{id}", COURSE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /courses/{id}")
    class Delete {

        @Test
        @DisplayName("should return 204 when course is deleted")
        void shouldReturn204WhenCourseIsDeleted() throws Exception {
            doNothing().when(courseService).delete(COURSE_ID);

            mockMvc.perform(delete("/courses/{id}", COURSE_ID))
                    .andExpect(status().isNoContent());

            verify(courseService).delete(COURSE_ID);
        }

        @Test
        @DisplayName("should return 404 when course does not exist")
        void shouldReturn404WhenCourseDoesNotExist() throws Exception {
            UUID id = UUID.randomUUID();
            doThrow(new ResourceNotFoundException(String.format(ErrorMessages.COURSE_NOT_FOUND, id)))
                    .when(courseService).delete(id);

            mockMvc.perform(delete("/courses/{id}", id))
                    .andExpect(status().isNotFound());
        }
    }
}
