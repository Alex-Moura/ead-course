package br.com.devalex.course.service.impl;

import br.com.devalex.course.dtos.course.CourseRequestDTO;
import br.com.devalex.course.dtos.course.CourseResponseDTO;
import br.com.devalex.course.enums.CourseStatus;
import br.com.devalex.course.exceptions.ErrorMessages;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static br.com.devalex.course.common.TestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
            when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> courseService.findById(COURSE_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(String.format(ErrorMessages.COURSE_NOT_FOUND, COURSE_ID));

            verifyNoInteractions(courseMapper);
        }
    }

    @Nested
    @DisplayName("findAll(Specification, Pageable)")
    class FindAllPaginated {

        @Test
        @DisplayName("should return a Page of DTOs when courses exist")
        void shouldReturnPageOfDTOs() {
            Course entity = courseEntity();
            CourseResponseDTO response = courseResponse();
            Page<Course> coursePage = new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1);

            when(courseRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(coursePage);
            when(courseMapper.toDTO(entity)).thenReturn(response);

            Specification<Course> noFilter = (root, query, cb) -> null;
            Page<CourseResponseDTO> result = courseService.findAll(noFilter, PageRequest.of(0, 10));

            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).id()).isEqualTo(COURSE_ID);
            verify(courseRepository).findAll(any(Specification.class), any(Pageable.class));
            verify(courseMapper).toDTO(entity);
        }

        @Test
        @DisplayName("should return an empty Page when no courses match the specification")
        void shouldReturnEmptyPageWhenNoCoursesMatch() {
            Page<Course> emptyPage = Page.empty(PageRequest.of(0, 10));

            when(courseRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(emptyPage);

            Specification<Course> noFilter = (root, query, cb) -> null;
            Page<CourseResponseDTO> result = courseService.findAll(noFilter, PageRequest.of(0, 10));

            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getContent()).isEmpty();
            verifyNoInteractions(courseMapper);
        }

        @Test
        @DisplayName("should respect pagination — return only the requested page")
        void shouldRespectPagination() {
            Course c2 = courseEntity();
            Page<Course> secondPage = new PageImpl<>(List.of(c2), PageRequest.of(1, 1), 2);

            when(courseRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(secondPage);
            when(courseMapper.toDTO(c2)).thenReturn(courseResponse());

            Specification<Course> noFilter = (root, query, cb) -> null;
            Page<CourseResponseDTO> result = courseService.findAll(noFilter, PageRequest.of(0, 10));

            assertThat(result.getNumber()).isEqualTo(1);
            assertThat(result.getSize()).isEqualTo(1);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getTotalPages()).isEqualTo(2);
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("should pass the exact specification through to the repository")
        void shouldDelegateSpecificationToRepository() {
            Specification<Course> spec = (root, query, cb) ->
                    cb.equal(root.get("courseStatus"), CourseStatus.INPROGRESS);

            Page<Course> coursePage = new PageImpl<>(List.of(courseEntity()));

            when(courseRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(coursePage);
            when(courseMapper.toDTO(any())).thenReturn(courseResponse());

            courseService.findAll(spec, PageRequest.of(0, 10));

            verify(courseRepository).findAll(eq(spec), any(Pageable.class));
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
            when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> courseService.update(COURSE_ID, courseRequest()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(String.format(ErrorMessages.COURSE_NOT_FOUND, COURSE_ID));

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
            when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> courseService.delete(COURSE_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(String.format(ErrorMessages.COURSE_NOT_FOUND, COURSE_ID));

            verify(courseRepository, never()).delete(any(Course.class));
        }
    }
}
