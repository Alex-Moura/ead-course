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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.com.devalex.course.common.TestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
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



    @Nested
    @DisplayName("findById()")
    class FindById{

        @Test
        @DisplayName("should return module when (moduleId, courseId) pair is found")
        void shouldReturnModuleWhenModuleAndCourseExist(){
            Module entity = moduleEntity();
            ModuleResponseDTO response = moduleResponse();

            when(moduleRepository.findByIdAndCourseId(MODULE_ID, COURSE_ID))
                    .thenReturn(Optional.of(entity));
            when(moduleMapper.toDTO(entity)).thenReturn(response);

            assertThat(moduleService.findById(MODULE_ID, COURSE_ID).id()).isEqualTo(MODULE_ID);

        }

        @Test
        @DisplayName("should throw an exception when the module does not belong to the course")
        void shouldThrowExceptionWhenModuleIsNotPartOfCourse(){
            when(moduleRepository.findByIdAndCourseId(MODULE_ID, COURSE_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> moduleService.findById(MODULE_ID, COURSE_ID))
                    .isInstanceOf(ResourceNotFoundException.class);

            verifyNoInteractions(moduleMapper);
        }
    }

    @Nested
    @DisplayName("findAllByCourseId()")
    class FindAll{

        @Test
        @DisplayName("should return all modules of the course")
        void shouldReturnAllCourseModules(){
            List<Module> entities = List.of(moduleEntity());
            List<ModuleResponseDTO> dtos = List.of(moduleResponse());

            when(moduleRepository.findAllByCourseId(COURSE_ID)).thenReturn(entities);
            when(moduleMapper.toDTOList(entities)).thenReturn(dtos);

            assertThat(moduleService.findAllByCourseId(COURSE_ID)).hasSize(1);
        }

        @Test
        @DisplayName("should return an empty list when the course has no modules")
        void shouldReturnEmptyListWhenCourseHasNoModules(){
            when(moduleRepository.findAllByCourseId(COURSE_ID)).thenReturn(List.of());
            when(moduleMapper.toDTOList(List.of())).thenReturn(List.of());

            assertThat(moduleService.findAllByCourseId(COURSE_ID)).isEmpty();
        }
    }
}
