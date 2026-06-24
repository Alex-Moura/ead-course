package br.com.devalex.course.controller;

import br.com.devalex.course.dtos.lesson.LessonResponseDTO;
import br.com.devalex.course.exceptions.ErrorMessages;
import br.com.devalex.course.exceptions.custom.ResourceNotFoundException;
import br.com.devalex.course.service.LessonService;
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


@WebMvcTest(LessonController.class)
@DisplayName("LessonController")
public class LessonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private LessonService lessonService;

    @Nested
    @DisplayName("POST /courses/{courseId}/modules/{moduleId}/lessons")
    class Save {

        @Test
        @DisplayName("should return 201 and the created lesson when request is valid")
        void shouldReturn201WhenRequestIsValid() throws Exception {
            when(lessonService.save(any(), eq(COURSE_ID), eq(MODULE_ID))).thenReturn(lessonResponse());

            mockMvc.perform(post("/courses/{courseId}/modules/{moduleId}/lessons", COURSE_ID, MODULE_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(lessonRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(LESSON_ID.toString()))
                    .andExpect(jsonPath("$.title").value("Aula 1 - O que é Spring?"));
        }

        @Test
        @DisplayName("should return 404 when module does not belong to the course")
        void shouldReturn404WhenModuleDoesNotBelongToCourse() throws Exception {
            UUID id = UUID.randomUUID();
            when(lessonService.save(any(), eq(COURSE_ID), eq(id)))
                    .thenThrow(new ResourceNotFoundException(
                            String.format(ErrorMessages.COURSE_NOT_FOUND, id)));

            mockMvc.perform(post("/courses/{courseId}/modules/{moduleId}/lessons", COURSE_ID, id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(lessonRequest())))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return 400 when title is blank")
        void shouldReturn400WhenTitleIsBlank() throws Exception {
            String body = """
                {
                  "title": "",
                  "description": "Descrição válida",
                  "videoUrl": "https://video.example.com/aula1.mp4"
                }
                """;

            mockMvc.perform(post("/courses/{courseId}/modules/{moduleId}/lessons", COURSE_ID, MODULE_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.title").exists());
        }

        @Test
        @DisplayName("should return 400 when videoUrl is blank")
        void shouldReturn400WhenDescriptionUrlIsBlank() throws Exception {
            String body = """
                {
                  "title": "Aula válida",
                  "description": "",
                  "videoUrl": "https://video.example.com/aula1.mp4"
                }
                """;

            mockMvc.perform(post("/courses/{courseId}/modules/{moduleId}/lessons", COURSE_ID, MODULE_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.description").exists());
        }

        @Test
        @DisplayName("should return 400 when videoUrl is blank")
        void shouldReturn400WhenVideoUrlIsBlank() throws Exception {
            String body = """
                {
                  "title": "Aula válida",
                  "description": "Descrição válida",
                  "videoUrl": ""
                }
                """;

            mockMvc.perform(post("/courses/{courseId}/modules/{moduleId}/lessons", COURSE_ID, MODULE_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.videoUrl").exists());
        }

        @Test
        @DisplayName("should return 400 when required fields are missing")
        void shouldReturn400WhenRequiredFieldsAreMissing() throws Exception {
            mockMvc.perform(post("/courses/{courseId}/modules/{moduleId}/lessons", COURSE_ID, MODULE_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", aMapWithSize(greaterThan(0))));
        }
    }

    @Nested
    @DisplayName("GET /courses/{courseId}/modules/{moduleId}/lessons")
    class FindAll {

        @Test
        @DisplayName("should return 200 with Page structure")
        void shouldReturn200WithPageStructure() throws Exception {
            Page<LessonResponseDTO> page = new PageImpl<>(
                    List.of(lessonResponse()), PageRequest.of(0, 10), 1);

            when(lessonService.findAllByModuleId(eq(COURSE_ID), eq(MODULE_ID), any(SpecificationTemplate.LessonSpec.class), any()))
                    .thenReturn(page);

            mockMvc.perform(get("/courses/{courseId}/modules/{moduleId}/lessons", COURSE_ID, MODULE_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].id").value(LESSON_ID.toString()))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        @DisplayName("should return 200 with empty content when module has no lessons")
        void shouldReturn200WithEmptyContent() throws Exception {
            when(lessonService.findAllByModuleId(eq(COURSE_ID), eq(MODULE_ID), any(SpecificationTemplate.LessonSpec.class), any()))
                    .thenReturn(Page.empty(PageRequest.of(0, 10)));

            mockMvc.perform(get("/courses/{courseId}/modules/{moduleId}/lessons", COURSE_ID, MODULE_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)));
        }

        @Test
        @DisplayName("should apply title filter from query param")
        void shouldApplyTitleFilter() throws Exception {
            Page<LessonResponseDTO> page = new PageImpl<>(List.of(lessonResponse()));
            when(lessonService.findAllByModuleId(eq(COURSE_ID), eq(MODULE_ID), any(SpecificationTemplate.LessonSpec.class), any()))
                    .thenReturn(page);

            mockMvc.perform(get("/courses/{courseId}/modules/{moduleId}/lessons", COURSE_ID, MODULE_ID)
                            .param("title", "Spring"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)));

            verify(lessonService).findAllByModuleId(eq(COURSE_ID), eq(MODULE_ID), any(SpecificationTemplate.LessonSpec.class), any());
        }

        @Test
        @DisplayName("should return 404 when module does not belong to the course")
        void shouldReturn404WhenModuleDoesNotBelongToCourse() throws Exception {
            UUID id = UUID.randomUUID();
            when(lessonService.findAllByModuleId(eq(COURSE_ID), eq(id), any(SpecificationTemplate.LessonSpec.class), any()))
                    .thenThrow(new ResourceNotFoundException(
                            String.format(ErrorMessages.MODULE_NOT_IN_COURSE, id, COURSE_ID)));

            mockMvc.perform(get("/courses/{courseId}/modules/{moduleId}/lessons", COURSE_ID, id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("GET /courses/{courseId}/modules/{moduleId}/lessons/{lessonId}")
    class FindById {

        @Test
        @DisplayName("should return 200 and the lesson when it exists")
        void shouldReturn200WhenLessonExists() throws Exception {
            when(lessonService.findById(LESSON_ID, COURSE_ID, MODULE_ID)).thenReturn(lessonResponse());

            mockMvc.perform(get("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}",
                            COURSE_ID, MODULE_ID, LESSON_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(LESSON_ID.toString()))
                    .andExpect(jsonPath("$.title").value("Aula 1 - O que é Spring?"));
        }

        @Test
        @DisplayName("should return 404 when lesson does not exist")
        void shouldReturn404WhenLessonDoesNotExist() throws Exception {
            UUID id = UUID.randomUUID();
            when(lessonService.findById(id, COURSE_ID, MODULE_ID))
                    .thenThrow(new ResourceNotFoundException(String.format(ErrorMessages.LESSON_NOT_FOUND, id)));

            mockMvc.perform(get("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}",
                            COURSE_ID, MODULE_ID, id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("PUT /courses/{courseId}/modules/{moduleId}/lessons/{lessonId}")
    class Update {

        @Test
        @DisplayName("should return 200 when update is successful")
        void shouldReturn200WhenUpdateIsSuccessful() throws Exception {
            when(lessonService.update(eq(LESSON_ID), eq(COURSE_ID), eq(MODULE_ID), any()))
                    .thenReturn(lessonResponse());

            mockMvc.perform(put("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}",
                            COURSE_ID, MODULE_ID, LESSON_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(lessonRequest())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(LESSON_ID.toString()));
        }

        @Test
        @DisplayName("should return 404 when lesson does not exist")
        void shouldReturn404WhenLessonDoesNotExist() throws Exception {
            UUID id = UUID.randomUUID();
            when(lessonService.update(eq(id), eq(COURSE_ID), eq(MODULE_ID), any()))
                    .thenThrow(new ResourceNotFoundException(String.format(ErrorMessages.LESSON_NOT_FOUND, id)));

            mockMvc.perform(put("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}",
                            COURSE_ID, MODULE_ID, id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(lessonRequest())))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return 400 when body is invalid")
        void shouldReturn400WhenBodyIsInvalid() throws Exception {
            mockMvc.perform(put("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}",
                            COURSE_ID, MODULE_ID, LESSON_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /courses/{courseId}/modules/{moduleId}/lessons/{lessonId}")
    class Delete {

        @Test
        @DisplayName("should return 204 when lesson is deleted")
        void shouldReturn204WhenLessonIsDeleted() throws Exception {
            doNothing().when(lessonService).delete(LESSON_ID, COURSE_ID, MODULE_ID);

            mockMvc.perform(delete("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}",
                            COURSE_ID, MODULE_ID, LESSON_ID))
                    .andExpect(status().isNoContent());

            verify(lessonService).delete(LESSON_ID, COURSE_ID, MODULE_ID);
        }

        @Test
        @DisplayName("should return 404 when lesson does not exist")
        void shouldReturn404WhenLessonDoesNotExist() throws Exception {
            UUID id = UUID.randomUUID();
            doThrow(new ResourceNotFoundException(String.format(ErrorMessages.LESSON_NOT_FOUND, id)))
                    .when(lessonService).delete(id, COURSE_ID, MODULE_ID);

            mockMvc.perform(delete("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}",
                            COURSE_ID, MODULE_ID, id))
                    .andExpect(status().isNotFound());
        }
    }
}
