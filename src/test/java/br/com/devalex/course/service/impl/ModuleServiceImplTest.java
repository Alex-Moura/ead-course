package br.com.devalex.course.service.impl;

import br.com.devalex.course.dtos.module.ModuleResponseDTO;
import br.com.devalex.course.exceptions.ErrorMessages;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static br.com.devalex.course.common.TestFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
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
            when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> moduleService.save(moduleRequest(), COURSE_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(String.format(ErrorMessages.COURSE_NOT_FOUND, COURSE_ID));

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
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(String.format(ErrorMessages.MODULE_NOT_IN_COURSE, MODULE_ID, COURSE_ID));

            verifyNoInteractions(moduleMapper);
        }
    }

//    @Nested
//    @DisplayName("findAllByCourseId()")
//    class FindAll{
//
//        @Test
//        @DisplayName("should return all modules of the course")
//        void shouldReturnAllCourseModules(){
//            List<Module> entities = List.of(moduleEntity());
//            List<ModuleResponseDTO> dtos = List.of(moduleResponse());
//
//            when(moduleRepository.findAllByCourseId(COURSE_ID)).thenReturn(entities);
//            when(moduleMapper.toDTOList(entities)).thenReturn(dtos);
//
//            assertThat(moduleService.findAllByCourseId(COURSE_ID)).hasSize(1);
//        }
//
//        @Test
//        @DisplayName("should return an empty list when the course has no modules")
//        void shouldReturnEmptyListWhenCourseHasNoModules(){
//            when(moduleRepository.findAllByCourseId(COURSE_ID)).thenReturn(List.of());
//            when(moduleMapper.toDTOList(List.of())).thenReturn(List.of());
//
//            assertThat(moduleService.findAllByCourseId(COURSE_ID)).isEmpty();
//        }
//    }

    @Nested
    @DisplayName("update()")
    class Update{
        @Test
        @DisplayName("should update module when found")
        void shouldUpdateModule(){
            Module entity = moduleEntity();
            ModuleResponseDTO response = moduleResponse();

            when(moduleRepository.findByIdAndCourseId(MODULE_ID, COURSE_ID))
                    .thenReturn(Optional.of(entity));
            when(moduleRepository.save(entity)).thenReturn(entity);
            when(moduleMapper.toDTO(entity)).thenReturn(response);

            ModuleResponseDTO result = moduleService.update(MODULE_ID, COURSE_ID, moduleRequest());

            assertThat(result).isNotNull();
            verify(moduleMapper).updateModuleFromDTO(moduleRequest(), entity);
            verify(moduleRepository).save(entity);

        }

        @Test
        @DisplayName("should throw an exception when updating a non-existent module")
        void shouldThrowExceptionWhenModuleIsNotPartOfCourse(){
            when(moduleRepository.findByIdAndCourseId(MODULE_ID, COURSE_ID))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> moduleService.update(MODULE_ID, COURSE_ID, moduleRequest()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(String.format(ErrorMessages.MODULE_NOT_IN_COURSE, MODULE_ID, COURSE_ID));

            verify(moduleRepository, never()).save(any());

        }
    }

    @Nested
    @DisplayName("findAllByCourseId()")
    class FindAll {

        @Test
        @DisplayName("should return a Page of modules when course exists")
        void shouldReturnPageOfModules() {
            Module entity = moduleEntity();
            ModuleResponseDTO response = moduleResponse();
            Specification<Module> spec = (root, query, cb) -> null;
            Pageable pageable = PageRequest.of(0, 10);
            Page<Module> page = new PageImpl<>(List.of(entity), pageable, 1);

            when(courseRepository.existsById(COURSE_ID)).thenReturn(true);
            when(moduleRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
            when(moduleMapper.toDTO(entity)).thenReturn(response);

            Page<ModuleResponseDTO> result = moduleService.findAllByCourseId(COURSE_ID, spec, pageable);

            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).id()).isEqualTo(MODULE_ID);
            verify(moduleRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("should return empty Page when course has no modules")
        void shouldReturnEmptyPage() {
            Specification<Module> spec = (root, query, cb) -> null;
            Pageable pageable = PageRequest.of(0, 10);

            when(courseRepository.existsById(COURSE_ID)).thenReturn(true);
            when(moduleRepository.findAll(any(Specification.class), eq(pageable)))
                    .thenReturn(Page.empty(pageable));

            Page<ModuleResponseDTO> result = moduleService.findAllByCourseId(COURSE_ID, spec, pageable);

            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getContent()).isEmpty();
            verifyNoInteractions(moduleMapper);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when course does not exist")
        void shouldThrowWhenCourseDoesNotExist() {
            Specification<Module> spec = (root, query, cb) -> null;
            Pageable pageable = PageRequest.of(0, 10);

            when(courseRepository.existsById(COURSE_ID)).thenReturn(false);

            assertThatThrownBy(() -> moduleService.findAllByCourseId(COURSE_ID, spec, pageable))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(String.format(ErrorMessages.COURSE_NOT_FOUND, COURSE_ID));

            verify(moduleRepository, never()).findAll(any(Specification.class), any(Pageable.class));
        }
    }
}
