package br.com.devalex.course.service.impl;

import br.com.devalex.course.dtos.course.CourseRequestDTO;
import br.com.devalex.course.dtos.course.CourseResponseDTO;
import br.com.devalex.course.exceptions.custom.ResourceNotFoundException;
import br.com.devalex.course.mapper.CourseMapper;
import br.com.devalex.course.model.Course;
import br.com.devalex.course.repository.CourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.com.devalex.course.common.TestFixtures.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseServiceImpl")
public class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private CourseMapper courseMapper;
    @InjectMocks
    private CourseServiceImpl courseService;

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("should save and return DTO when data is valid ")
        void shouldSaveAndReturnDTO() {
            CourseRequestDTO request = courseRequest();
            Course entity = courseEntity();
            CourseResponseDTO response = courseResponse();

            when(courseMapper.toEntity(request)).thenReturn(entity);
            when(courseRepository.save(entity)).thenReturn(entity);
            when(courseMapper.toDTO(entity)).thenReturn(response);

            CourseResponseDTO result = courseService.save(request);

            assertThat(result.id()).isEqualTo(COURSE_ID);
            assertThat(result.name()).isEqualTo("Spring Boot na Prática");
            verify(courseMapper).toEntity(request);
            verify(courseRepository).save(entity);
            verify(courseMapper).toDTO(entity);
        }

        @Test
        @DisplayName("should propagate an exception if the repository fails")
        void shouldPropagateRepositoryException () {
            Course entity = courseEntity();
            when(courseMapper.toEntity(any())).thenReturn(entity);
            when(courseRepository.save(any())).thenThrow(new RuntimeException("DB offline"));

            assertThatThrownBy(() -> courseService.save(courseRequest()))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("DB offline");
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById{

        @Test
        @DisplayName("should return DTO when course exists")
        void shouldReturnDTOWhenCourseExists() {
            Course entity = courseEntity();
            CourseResponseDTO response = courseResponse();

            when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(entity));
            when(courseMapper.toDTO(entity)).thenReturn(response);

            CourseResponseDTO result = courseService.findById(COURSE_ID);

            assertThat(result.id()).isEqualTo(COURSE_ID);
            verify(courseRepository).findById(COURSE_ID);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when course does not exist")
        void shouldThrowExceptionWhenCourseDoesNotExist(){
            UUID id = UUID.randomUUID();
            when(courseRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> courseService.findById(id))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Curso com id: " + id + " não encontrado");

            verifyNoInteractions(courseMapper);
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAll{

        @Test
        @DisplayName("should return a list of DTOs when there are courses")
        void shouldReturnListOfDTOs(){
            List<Course> entities = List.of(courseEntity());
            List<CourseResponseDTO> dtos = List.of(courseResponse());

            when(courseRepository.findAll()).thenReturn(entities);
            when(courseMapper.toDTOList(entities)).thenReturn(dtos);

            assertThat(courseService.findAll()).hasSize(1);
        }

        @Test
        @DisplayName("should return an empty list when there are no courses")
        void shouldReturnEmptyList(){

            when(courseRepository.findAll()).thenReturn(List.of());
            when(courseMapper.toDTOList(List.of())).thenReturn(List.of());

            assertThat(courseService.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("update()")
    class update{

        @Test
        @DisplayName("should update and return DTO when course exists")
        void shouldUpdateAndReturnDTO(){
            CourseRequestDTO request = courseRequest();
            Course entity = courseEntity();
            CourseResponseDTO response = courseResponse();

            when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(entity));
            when(courseRepository.save(entity)).thenReturn(entity);
            when(courseMapper.toDTO(entity)).thenReturn(response);

            CourseResponseDTO result = courseService.update(COURSE_ID, request);

            assertThat(result).isNotNull();
            verify(courseMapper).updateCourseFromDTO(request, entity);
            verify(courseRepository).save(entity);
        }

        @Test
        @DisplayName("should throw an exception when the course does not exist during update")
        void shouldThrowExceptionWhenCourseDoesNotExist(){
            UUID id = UUID.randomUUID();
            when(courseRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> courseService.update(id, courseRequest()))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(courseRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete()")
    class delete{

        @Test
        @DisplayName("should delete when course exists")
        void shouldDeleteWhenCourseExists(){
            Course entity = courseEntity();
            when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(entity));

            assertThatCode(() -> courseService.delete(COURSE_ID)).doesNotThrowAnyException();

            verify(courseRepository).delete(entity);
        }

        @Test
        @DisplayName("should throw an exception when deleting a non-existent course")
        void shouldThrowExceptionWhenDeletingNonExistentCourse(){
            UUID id = UUID.randomUUID();
            when(courseRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> courseService.delete(id))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(courseRepository, never()).delete(any(Course.class));
        }
    }
}
