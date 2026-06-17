package br.com.devalex.course.controller;


import br.com.devalex.course.dtos.course.CourseResponseDTO;
import br.com.devalex.course.enums.CourseLevel;
import br.com.devalex.course.enums.CourseStatus;
import br.com.devalex.course.exceptions.ErrorMessages;
import br.com.devalex.course.exceptions.custom.ResourceNotFoundException;
import br.com.devalex.course.service.CourseService;
import br.com.devalex.course.specification.SpecificationTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
        @DisplayName("should return 200 with Page structure and default pagination")
        void shouldReturn200WithPageStructure() throws Exception {
            Page<CourseResponseDTO> page = new PageImpl<>(
                    List.of(courseResponse()),
                    PageRequest.of(0, 10),
                    1
            );
            when(courseService.findAll(any(SpecificationTemplate.CourseSpec.class), any()))
                    .thenReturn(page);

            mockMvc.perform(get("/courses"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].id").value(COURSE_ID.toString()))
                    .andExpect(jsonPath("$.content[0].name").value("Spring Boot na Prática"))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.number").value(0))
                    .andExpect(jsonPath("$.size").value(10));
        }

        @Test
        @DisplayName("should return 200 with empty content when no courses match")
        void shouldReturn200WithEmptyContent() throws Exception {
            Page<CourseResponseDTO> emptyPage = Page.empty(PageRequest.of(0, 10));
            when(courseService.findAll(any(SpecificationTemplate.CourseSpec.class), any()))
                    .thenReturn(emptyPage);

            mockMvc.perform(get("/courses"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)))
                    .andExpect(jsonPath("$.totalElements").value(0));
        }

        @Test
        @DisplayName("should apply name filter from query param")
        void shouldApplyNameFilter() throws Exception {
            Page<CourseResponseDTO> page = new PageImpl<>(List.of(courseResponse()));
            when(courseService.findAll(any(SpecificationTemplate.CourseSpec.class), any()))
                    .thenReturn(page);

            mockMvc.perform(get("/courses")
                            .param("name", "Spring"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)));

            verify(courseService).findAll(any(SpecificationTemplate.CourseSpec.class), any());
        }

        @Test
        @DisplayName("should apply courseStatus filter from query param")
        void shouldApplyCourseStatusFilter() throws Exception {
            Page<CourseResponseDTO> page = new PageImpl<>(List.of(courseResponse()));
            when(courseService.findAll(any(SpecificationTemplate.CourseSpec.class), any()))
                    .thenReturn(page);

            mockMvc.perform(get("/courses")
                            .param("courseStatus", CourseStatus.INPROGRESS.name()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].courseStatus").value("INPROGRESS"));
        }

        @Test
        @DisplayName("should apply courseLevel filter from query param")
        void shouldApplyCourseLevelFilter() throws Exception {
            Page<CourseResponseDTO> page = new PageImpl<>(List.of(courseResponse()));
            when(courseService.findAll(any(SpecificationTemplate.CourseSpec.class), any()))
                    .thenReturn(page);

            mockMvc.perform(get("/courses")
                            .param("courseLevel", CourseLevel.BEGINNER.name()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].courseLevel").value("BEGINNER"));
        }

        @Test
        @DisplayName("should apply combined filters from multiple query params")
        void shouldApplyCombinedFilters() throws Exception {
            Page<CourseResponseDTO> page = new PageImpl<>(List.of(courseResponse()));
            when(courseService.findAll(any(SpecificationTemplate.CourseSpec.class), any()))
                    .thenReturn(page);

            mockMvc.perform(get("/courses")
                            .param("name", "Spring")
                            .param("courseStatus", CourseStatus.INPROGRESS.name())
                            .param("courseLevel", CourseLevel.BEGINNER.name()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)));

            verify(courseService).findAll(any(SpecificationTemplate.CourseSpec.class), any());
        }

        @Test
        @DisplayName("should respect custom page and size params")
        void shouldRespectCustomPageAndSize() throws Exception {
            Page<CourseResponseDTO> secondPage = new PageImpl<>(
                    List.of(courseResponse()),
                    PageRequest.of(1, 5),
                    6
            );
            when(courseService.findAll(any(SpecificationTemplate.CourseSpec.class), any()))
                    .thenReturn(secondPage);

            mockMvc.perform(get("/courses")
                            .param("page", "1")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.number").value(1))
                    .andExpect(jsonPath("$.size").value(5))
                    .andExpect(jsonPath("$.totalElements").value(6))
                    .andExpect(jsonPath("$.totalPages").value(2));
        }

        @Test
        @DisplayName("should use default pagination when no params are provided")
        void shouldUseDefaultPagination() throws Exception {
            Page<CourseResponseDTO> page = new PageImpl<>(
                    List.of(courseResponse()),
                    PageRequest.of(0, 10),
                    1
            );
            when(courseService.findAll(any(SpecificationTemplate.CourseSpec.class), any()))
                    .thenReturn(page);

            mockMvc.perform(get("/courses"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.number").value(0))
                    .andExpect(jsonPath("$.size").value(10));
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
