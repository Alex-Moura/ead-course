package br.com.devalex.course.controller;

import br.com.devalex.course.dtos.module.ModuleResponseDTO;
import br.com.devalex.course.exceptions.ErrorMessages;
import br.com.devalex.course.exceptions.custom.ResourceNotFoundException;
import br.com.devalex.course.service.ModuleService;
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
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ModuleController.class)
@DisplayName("ModuleController")
public class ModuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ModuleService moduleService;

    @Nested
    @DisplayName("POST /courses/{courseId}/modules")
    class Save {

        @Test
        @DisplayName("should return 201 and the created module when request is valid")
        void shouldReturn201WhenRequestIsValid() throws Exception {
            when(moduleService.save(any(), eq(COURSE_ID))).thenReturn(moduleResponse());

            mockMvc.perform(post("/courses/{courseId}/modules", COURSE_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(moduleRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(MODULE_ID.toString()))
                    .andExpect(jsonPath("$.title").value("Módulo 1 - Fundamentos"));
        }

        @Test
        @DisplayName("should return 404 when course does not exist")
        void shouldReturn404WhenCourseDoesNotExist() throws Exception {
            UUID id = UUID.randomUUID();
            when(moduleService.save(any(), eq(id)))
                    .thenThrow(new ResourceNotFoundException(String.format(ErrorMessages.COURSE_NOT_FOUND, id)));

            mockMvc.perform(post("/courses/{courseId}/modules", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(moduleRequest())))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return 400 when title is blank")
        void shouldReturn400WhenTitleIsBlank() throws Exception {
            String body = """
                {
                  "title": "",
                  "description": "Descrição válida"
                }
                """;

            mockMvc.perform(post("/courses/{courseId}/modules", COURSE_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.title").exists());
        }

        @Test
        @DisplayName("should return 400 when description is blank")
        void shouldReturn400WhenDescriptionIsBlank() throws Exception {
            String body = """
                {
                  "title": "Módulo válido",
                  "description": ""
                }
                """;

            mockMvc.perform(post("/courses/{courseId}/modules", COURSE_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.description").exists());
        }
    }

    @Nested
    @DisplayName("GET /courses/{courseId}/modules")
    class FindAll {

        @Test
        @DisplayName("should return 200 with Page structure")
        void shouldReturn200WithPageStructure() throws Exception {
            Page<ModuleResponseDTO> page = new PageImpl<>(
                    List.of(moduleResponse()), PageRequest.of(0, 10), 1);

            when(moduleService.findAllByCourseId(eq(COURSE_ID), any(SpecificationTemplate.ModuleSpec.class), any()))
                    .thenReturn(page);

            mockMvc.perform(get("/courses/{courseId}/modules", COURSE_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].id").value(MODULE_ID.toString()))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.number").value(0))
                    .andExpect(jsonPath("$.size").value(10));
        }

        @Test
        @DisplayName("should return 200 with empty content when course has no modules")
        void shouldReturn200WithEmptyContent() throws Exception {
            when(moduleService.findAllByCourseId(eq(COURSE_ID), any(SpecificationTemplate.ModuleSpec.class), any()))
                    .thenReturn(Page.empty(PageRequest.of(0, 10)));

            mockMvc.perform(get("/courses/{courseId}/modules", COURSE_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)))
                    .andExpect(jsonPath("$.totalElements").value(0));
        }

        @Test
        @DisplayName("should apply title filter from query param")
        void shouldApplyTitleFilter() throws Exception {
            Page<ModuleResponseDTO> page = new PageImpl<>(List.of(moduleResponse()));
            when(moduleService.findAllByCourseId(eq(COURSE_ID), any(SpecificationTemplate.ModuleSpec.class), any()))
                    .thenReturn(page);

            mockMvc.perform(get("/courses/{courseId}/modules", COURSE_ID)
                            .param("title", "Fundamentos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)));

            verify(moduleService).findAllByCourseId(eq(COURSE_ID), any(SpecificationTemplate.ModuleSpec.class), any());
        }

        @Test
        @DisplayName("should return 404 when course does not exist")
        void shouldReturn404WhenCourseDoesNotExist() throws Exception {
            UUID id = UUID.randomUUID();
            when(moduleService.findAllByCourseId(eq(id), any(SpecificationTemplate.ModuleSpec.class), any()))
                    .thenThrow(new ResourceNotFoundException(
                            String.format(ErrorMessages.COURSE_NOT_FOUND, id)));

            mockMvc.perform(get("/courses/{courseId}/modules", id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("GET /courses/{courseId}/modules/{moduleId}")
    class FindById {

        @Test
        @DisplayName("should return 200 and the module when it exists")
        void shouldReturn200WhenModuleExists() throws Exception {
            when(moduleService.findById(MODULE_ID, COURSE_ID)).thenReturn(moduleResponse());

            mockMvc.perform(get("/courses/{courseId}/modules/{moduleId}", COURSE_ID, MODULE_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(MODULE_ID.toString()));
        }

        @Test
        @DisplayName("should return 404 when module does not belong to the course")
        void shouldReturn404WhenModuleNotFound() throws Exception {
            UUID id = UUID.randomUUID();
            when(moduleService.findById(id, COURSE_ID))
                    .thenThrow(new ResourceNotFoundException(
                            String.format(ErrorMessages.MODULE_NOT_IN_COURSE, id, COURSE_ID)));

            mockMvc.perform(get("/courses/{courseId}/modules/{moduleId}", COURSE_ID, id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("PUT /courses/{courseId}/modules/{moduleId}")
    class Update {

        @Test
        @DisplayName("should return 200 when update is successful")
        void shouldReturn200WhenUpdateIsSuccessful() throws Exception {
            when(moduleService.update(eq(MODULE_ID), eq(COURSE_ID), any())).thenReturn(moduleResponse());

            mockMvc.perform(put("/courses/{courseId}/modules/{moduleId}", COURSE_ID, MODULE_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(moduleRequest())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(MODULE_ID.toString()));
        }

        @Test
        @DisplayName("should return 404 when module does not exist")
        void shouldReturn404WhenModuleDoesNotExist() throws Exception {
            UUID id = UUID.randomUUID();
            when(moduleService.update(eq(id), eq(COURSE_ID), any()))
                    .thenThrow(new ResourceNotFoundException(
                            String.format(ErrorMessages.MODULE_NOT_IN_COURSE, id, COURSE_ID)));

            mockMvc.perform(put("/courses/{courseId}/modules/{moduleId}", COURSE_ID, id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(moduleRequest())))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return 400 when body is invalid")
        void shouldReturn400WhenBodyIsInvalid() throws Exception {
            mockMvc.perform(put("/courses/{courseId}/modules/{moduleId}", COURSE_ID, MODULE_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /courses/{courseId}/modules/{moduleId}")
    class Delete {

        @Test
        @DisplayName("should return 204 when module is deleted")
        void shouldReturn204WhenModuleIsDeleted() throws Exception {
            doNothing().when(moduleService).delete(MODULE_ID, COURSE_ID);

            mockMvc.perform(delete("/courses/{courseId}/modules/{moduleId}", COURSE_ID, MODULE_ID))
                    .andExpect(status().isNoContent());

            verify(moduleService).delete(MODULE_ID, COURSE_ID);
        }

        @Test
        @DisplayName("should return 404 when module does not exist")
        void shouldReturn404WhenModuleDoesNotExist() throws Exception {
            UUID id = UUID.randomUUID();
            doThrow(new ResourceNotFoundException(
                    String.format(ErrorMessages.MODULE_NOT_IN_COURSE, id, COURSE_ID)))
                    .when(moduleService).delete(id, COURSE_ID);

            mockMvc.perform(delete("/courses/{courseId}/modules/{moduleId}", COURSE_ID, id))
                    .andExpect(status().isNotFound());
        }
    }
}
