package br.com.devalex.course.service.impl;

import br.com.devalex.course.dtos.lessons.LessonResponseDTO;
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
}
