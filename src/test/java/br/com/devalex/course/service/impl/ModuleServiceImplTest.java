package br.com.devalex.course.service.impl;

import br.com.devalex.course.dtos.module.ModuleResponseDTO;
import br.com.devalex.course.exceptions.custom.ResourceNotFoundException;
import br.com.devalex.course.mapper.ModuleMapper;
import br.com.devalex.course.model.Course;
import br.com.devalex.course.model.Module;
import br.com.devalex.course.repository.CourseRepository;
import br.com.devalex.course.repository.ModuleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static br.com.devalex.course.common.TestFixtures.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ModuleServiceImpl")
public class ModuleServiceImplTest {

    @Mock private ModuleRepository moduleRepository;
    @Mock private ModuleMapper moduleMapper;
    @Mock private CourseRepository courseRepository;
    @InjectMocks private ModuleServiceImpl moduleService;

    @Nested
    @DisplayName("save()")
    class save{

        @Test
        @DisplayName("should save module associated with course")
        void shouldSaveModuleForExistingCourse (){
            Course course = courseEntity();
            Module entity = moduleEntity();
            ModuleResponseDTO response = moduleResponse();

            when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(course));
            when(moduleMapper.toEntity(moduleRequest())).thenReturn(entity);
            when(moduleRepository.save(entity)).thenReturn(entity);
            when(moduleMapper.toDTO(entity)).thenReturn(response);

            ModuleResponseDTO result = moduleService.save(moduleRequest(), COURSE_ID);

            assertThat(result.id()).isEqualTo(MODULE_ID);
            verify(moduleRepository).save(entity);

        }

        @Test
        @DisplayName("should throw an exception when the course does not exist")
        void shouldThrowResourceNotFoundExceptionWhenCourseDoesNotExist(){
            UUID id = UUID.randomUUID();

            when(courseRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> moduleService.save(moduleRequest(), id))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Curso com id: %s não encontrado", id);

            verifyNoInteractions(moduleRepository);
        }
    }
}
