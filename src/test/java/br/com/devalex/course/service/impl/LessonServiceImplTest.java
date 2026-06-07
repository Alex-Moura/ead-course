package br.com.devalex.course.service.impl;

import br.com.devalex.course.dtos.lessons.LessonRequestDTO;
import br.com.devalex.course.dtos.lessons.LessonResponseDTO;
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
            Module module = moduleEntity();
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
    class FindAllByModuleId{

        @Test
        @DisplayName("should return a list of lessons when the module belongs to the course")
        void shouldReturnListOfLessons(){
            List<Lesson> entities = List.of(lessonEntity());
            List<LessonResponseDTO> dtos = List.of(lessonResponse());

            when(moduleRepository.existsByIdAndCourseId(MODULE_ID, COURSE_ID)).thenReturn(true);
            when(lessonRepository.findAllByModuleId(MODULE_ID)).thenReturn(entities);
            when(lessonMapper.toDTOList(entities)).thenReturn(dtos);

            List<LessonResponseDTO> result = lessonService.findAllByModuleId(COURSE_ID, MODULE_ID);

            assertThat(result).hasSize(1).containsExactlyElementsOf(dtos);
        }

        @Test
        @DisplayName("should return an empty list when the module has no lessons")
        void shouldReturnEmptyListWhenModuleHasNoLessons(){
            when(moduleRepository.existsByIdAndCourseId(MODULE_ID, COURSE_ID)).thenReturn(true);
            when(lessonRepository.findAllByModuleId(MODULE_ID)).thenReturn(List.of());
            when(lessonMapper.toDTOList(List.of())).thenReturn(List.of());

            List<LessonResponseDTO> result = lessonService.findAllByModuleId(COURSE_ID, MODULE_ID);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when the module does not belong to the course")
        void shouldThrowExceptionWhenModuleDoesNotBelongToCourse(){
            when(moduleRepository.existsByIdAndCourseId(MODULE_ID, COURSE_ID)).thenReturn(false);

            assertThatThrownBy(() -> lessonService.findAllByModuleId(COURSE_ID, MODULE_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(String.format(ErrorMessages.MODULE_NOT_IN_COURSE, MODULE_ID, COURSE_ID));

            verify(lessonRepository, never()).findAllByModuleId(any());
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
                    .hasMessage(String.format(ErrorMessages.LESSON_NOT_FOUND, LESSON_ID, MODULE_ID));

            verify(lessonMapper, never()).updateLessonFromDTO(any(), any());
            verify(lessonRepository, never()).save(any());
        }
    }
}
