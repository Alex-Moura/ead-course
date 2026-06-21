package br.com.devalex.course.service.impl;

import br.com.devalex.course.dtos.module.ModuleRequestDTO;
import br.com.devalex.course.dtos.module.ModuleResponseDTO;
import br.com.devalex.course.exceptions.ErrorMessages;
import br.com.devalex.course.exceptions.custom.ResourceNotFoundException;
import br.com.devalex.course.mapper.ModuleMapper;
import br.com.devalex.course.model.Course;
import br.com.devalex.course.model.Module;
import br.com.devalex.course.repository.CourseRepository;
import br.com.devalex.course.repository.ModuleRepository;
import br.com.devalex.course.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModuleServiceImpl implements ModuleService {
    private final ModuleRepository moduleRepository;
    private final ModuleMapper moduleMapper;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public ModuleResponseDTO save(ModuleRequestDTO dto, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(()-> new ResourceNotFoundException(String.format(ErrorMessages.COURSE_NOT_FOUND, courseId)));
        Module module = moduleMapper.toEntity(dto);
        module.setCourse(course);
        return moduleMapper.toDTO(moduleRepository.save(module));
    }

    @Override
    public ModuleResponseDTO findById(UUID moduleId, UUID courseId) {
        return moduleRepository.findByIdAndCourseId(moduleId, courseId)
                .map(moduleMapper::toDTO)
                .orElseThrow(()-> new ResourceNotFoundException(String.format(ErrorMessages.MODULE_NOT_IN_COURSE, moduleId, courseId)));
    }

    @Override
    public Page<ModuleResponseDTO> findAllByCourseId(UUID courseId, Specification<Module> spec, Pageable pageable) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException(
                    String.format(ErrorMessages.COURSE_NOT_FOUND, courseId));
        }

        Specification<Module> byCourseId = (root, query, cb) ->
                cb.equal(root.get("course").get("id"), courseId);

        Specification<Module> combined = Specification.where(byCourseId).and(spec);

        return moduleRepository.findAll(combined, pageable)
                .map(moduleMapper::toDTO);
    }

    @Override
    @Transactional
    public ModuleResponseDTO update(UUID moduleId, UUID courseId, ModuleRequestDTO dto){
        Module module = moduleRepository.findByIdAndCourseId(moduleId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.MODULE_NOT_IN_COURSE, moduleId, courseId)));
        moduleMapper.updateModuleFromDTO(dto, module);
        return moduleMapper.toDTO(moduleRepository.save(module));

    }

    @Override
    @Transactional
    public void delete(UUID moduleId, UUID courseId) {
        Module module = moduleRepository.findByIdAndCourseId(moduleId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.MODULE_NOT_IN_COURSE, moduleId, courseId)));
        moduleRepository.delete(module);
    }
}
