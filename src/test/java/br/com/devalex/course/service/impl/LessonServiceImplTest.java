package br.com.devalex.course.service.impl;

import br.com.devalex.course.dtos.lesson.LessonRequestDTO;
import br.com.devalex.course.dtos.lesson.LessonResponseDTO;
import br.com.devalex.course.exceptions.ErrorMessages;
import br.com.devalex.course.exceptions.custom.ResourceNotFoundException;
import br.com.devalex.course.mapper.LessonMapper;
import br.com.devalex.course.model.Lesson;
import br.com.devalex.course.model.Module;
import br.com.devalex.course.repository.LessonRepository;
import br.com.devalex.course.repository.ModuleRepository;
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
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static br.com.devalex.course.common.TestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LessonServiceImpl")
public class LessonServiceImplTest {

    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private ModuleRepository moduleRepository;
    @Mock
    private LessonMapper lessonMapper;
    @InjectMocks
    private LessonServiceImpl lessonService;

    @Nested
    @DisplayName("save()")
    class Save{

        @Test
        @DisplayName("should save lesson associated with the correct module")
        void shouldSaveLessonAssociatedWithCorrectModule(){
            Module module = moduleEntity();
            Lesson entity = lessonEntity();
            LessonResponseDTO response = lessonResponse();

            when(moduleRepository.findByIdAndCourseId(MODULE_ID, COURSE_ID))
                    .thenReturn(Optional.of(module));
            when(lessonMapper.toEntity(lessonRequest())).thenReturn(entity);
            when(lessonRepository.save(entity)).thenReturn(entity);
            when(lessonMapper.toDTO(entity)).thenReturn(response);

            LessonResponseDTO result = lessonService.save(
                    lessonRequest(), COURSE_ID, MODULE_ID);

            assertThat(result.id()).isEqualTo(LESSON_ID);
            verify(lessonRepository).save(entity);
        }

        @Test
        @DisplayName("should throw an exception and not save if the module does not belong to the course")
        void shouldNotSaveLessonWhenModuleDoesNotBelongToCourse(){
            when(moduleRepository.findByIdAndCourseId(MODULE_ID, COURSE_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> lessonService.save(lessonRequest(), COURSE_ID, MODULE_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining(
                            "Módulo com id: " + MODULE_ID + " não encontrado no curso: " + COURSE_ID);

            verifyNoInteractions(lessonRepository);
            verifyNoInteractions(lessonMapper);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById{

        @Test
        @DisplayName("should find lesson by id")
        void shouldReturnLessonById(){
            Lesson entity = lessonEntity();
            LessonResponseDTO response = lessonResponse();

            when(moduleRepository.existsByIdAndCourseId(MODULE_ID, COURSE_ID)).thenReturn(true);
            when(lessonRepository.findByIdAndModuleId(LESSON_ID, MODULE_ID))
                    .thenReturn(Optional.of(entity));
            when(lessonMapper.toDTO(entity)).thenReturn(response);

            LessonResponseDTO result = lessonService.findById(LESSON_ID, COURSE_ID, MODULE_ID);

            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when the module does not belong to the course")
        void shouldThrowExceptionWhenModuleDoesNotBelongToCourse(){
            when(moduleRepository.existsByIdAndCourseId(MODULE_ID, COURSE_ID)).thenReturn(false);

            assertThatThrownBy(() -> lessonService.findById(LESSON_ID, COURSE_ID, MODULE_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(String.format(ErrorMessages.MODULE_NOT_IN_COURSE, MODULE_ID, COURSE_ID));

            verify(lessonRepository, never()).findByIdAndModuleId(any(), any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when the lesson does not belong to the module")
        void shouldThrowExceptionWhenLessonDoesNotBelongToModule(){
            when(moduleRepository.existsByIdAndCourseId(MODULE_ID, COURSE_ID)).thenReturn(true);
            when(lessonRepository.findByIdAndModuleId(LESSON_ID, MODULE_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> lessonService.findById(LESSON_ID, COURSE_ID, MODULE_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(String.format(ErrorMessages.LESSON_NOT_FOUND, LESSON_ID));
        }
    }

    @Nested
    @DisplayName("findAllByModuleId()")
    class FindAllByModuleId {

        @Test
        @DisplayName("should return a Page of lessons when module belongs to the course")
        void shouldReturnPageOfLessons() {
            Lesson entity = lessonEntity();
            LessonResponseDTO response = lessonResponse();
            Specification<Lesson> spec = (root, query, cb) -> null;
            Pageable pageable = PageRequest.of(0, 10);
            Page<Lesson> page = new PageImpl<>(List.of(entity), pageable, 1);

            when(moduleRepository.existsByIdAndCourseId(MODULE_ID, COURSE_ID)).thenReturn(true);
            when(lessonRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
            when(lessonMapper.toDTO(entity)).thenReturn(response);

            Page<LessonResponseDTO> result = lessonService.findAllByModuleId(COURSE_ID, MODULE_ID, spec, pageable);

            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent()).hasSize(1);
            verify(lessonRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("should return empty Page when module has no lessons")
        void shouldReturnEmptyPage() {
            Specification<Lesson> spec = (root, query, cb) -> null;
            Pageable pageable = PageRequest.of(0, 10);

            when(moduleRepository.existsByIdAndCourseId(MODULE_ID, COURSE_ID)).thenReturn(true);
            when(lessonRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(Page.empty(pageable));

            Page<LessonResponseDTO> result = lessonService.findAllByModuleId(COURSE_ID, MODULE_ID, spec, pageable);

            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getContent()).isEmpty();
            verifyNoInteractions(lessonMapper);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when module does not belong to course")
        void shouldThrowWhenModuleDoesNotBelongToCourse() {
            Specification<Lesson> spec = (root, query, cb) -> null;
            Pageable pageable = PageRequest.of(0, 10);

            when(moduleRepository.existsByIdAndCourseId(MODULE_ID, COURSE_ID)).thenReturn(false);

            assertThatThrownBy(() -> lessonService.findAllByModuleId(COURSE_ID, MODULE_ID, spec, pageable))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(String.format(ErrorMessages.MODULE_NOT_IN_COURSE, MODULE_ID, COURSE_ID));

            verify(lessonRepository, never()).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("should successfully update the lesson")
        void shouldUpdateLessonSuccessfully() {
            Lesson entity = lessonEntity();
            LessonRequestDTO request = lessonRequest();
            LessonResponseDTO response = lessonResponse();

            when(moduleRepository.existsByIdAndCourseId(MODULE_ID, COURSE_ID)).thenReturn(true);
            when(lessonRepository.findByIdAndModuleId(LESSON_ID, MODULE_ID))
                    .thenReturn(Optional.of(entity));
            when(lessonRepository.save(entity)).thenReturn(entity);
            when(lessonMapper.toDTO(entity)).thenReturn(response);

            LessonResponseDTO result = lessonService.update(LESSON_ID, COURSE_ID, MODULE_ID, request);

            assertThat(result).isEqualTo(response);
            verify(moduleRepository).existsByIdAndCourseId(MODULE_ID, COURSE_ID);
            verify(lessonMapper).updateLessonFromDTO(request, entity);
            verify(lessonRepository).save(entity);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when the module does not belong to the course")
        void shouldThrowExceptionWhenModuleDoesNotBelongToCourse() {
            LessonRequestDTO request = lessonRequest();

            when(moduleRepository.existsByIdAndCourseId(MODULE_ID, COURSE_ID)).thenReturn(false);

            assertThatThrownBy(() -> lessonService.update(LESSON_ID, COURSE_ID, MODULE_ID, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(String.format(ErrorMessages.MODULE_NOT_IN_COURSE, MODULE_ID, COURSE_ID));

            verify(lessonRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when the lesson does not belong to the module")
        void shouldThrowExceptionWhenLessonDoesNotBelongToModule() {
            LessonRequestDTO request = lessonRequest();

            when(moduleRepository.existsByIdAndCourseId(MODULE_ID, COURSE_ID)).thenReturn(true);
            when(lessonRepository.findByIdAndModuleId(LESSON_ID, MODULE_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> lessonService.update(LESSON_ID, COURSE_ID, MODULE_ID, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(String.format(ErrorMessages.LESSON_NOT_FOUND, LESSON_ID));

            verify(lessonMapper, never()).updateLessonFromDTO(any(), any());
            verify(lessonRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("should successfully delete the lesson")
        void shouldDeleteLessonSuccessfully() {
            Lesson entity = lessonEntity();

            when(moduleRepository.existsByIdAndCourseId(MODULE_ID, COURSE_ID)).thenReturn(true);
            when(lessonRepository.findByIdAndModuleId(LESSON_ID, MODULE_ID))
                    .thenReturn(Optional.of(entity));

            lessonService.delete(LESSON_ID, COURSE_ID, MODULE_ID);

            verify(lessonRepository).delete(entity);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when the module does not belong to the course")
        void shouldThrowExceptionWhenModuleDoesNotBelongToCourse() {

            when(moduleRepository.existsByIdAndCourseId(MODULE_ID, COURSE_ID)).thenReturn(false);

            assertThatThrownBy(() -> lessonService.delete(LESSON_ID, COURSE_ID, MODULE_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(String.format(ErrorMessages.MODULE_NOT_IN_COURSE, MODULE_ID, COURSE_ID));

            verify(lessonRepository, never()).delete(any(Lesson.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when the lesson does not belong to the module")
        void shouldThrowExceptionWhenLessonDoesNotBelongToModule() {
            when(moduleRepository.existsByIdAndCourseId(MODULE_ID, COURSE_ID)).thenReturn(true);
            when(lessonRepository.findByIdAndModuleId(LESSON_ID, MODULE_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> lessonService.delete(LESSON_ID, COURSE_ID, MODULE_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(String.format(ErrorMessages.LESSON_NOT_FOUND, LESSON_ID));

            verify(lessonRepository, never()).delete(any(Lesson.class));
        }
    }
}
