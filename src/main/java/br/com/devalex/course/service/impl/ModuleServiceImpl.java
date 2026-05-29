package br.com.devalex.course.service.impl;

import br.com.devalex.course.dtos.module.ModuleRequestDTO;
import br.com.devalex.course.dtos.module.ModuleResponseDTO;
import br.com.devalex.course.exceptions.custom.ResourceNotFoundException;
import br.com.devalex.course.mapper.ModuleMapper;
import br.com.devalex.course.model.Course;
import br.com.devalex.course.model.Module;
import br.com.devalex.course.repository.CourseRepository;
import br.com.devalex.course.repository.ModuleRepository;
import br.com.devalex.course.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
                .orElseThrow(()-> new ResourceNotFoundException("Curso com id: " + courseId + " não encontrado"));
        Module module = moduleMapper.toEntity(dto);
        module.setCourse(course);
        return moduleMapper.toDTO(moduleRepository.save(module));
    }

    @Override
    public ModuleResponseDTO findById(UUID moduleId, UUID courseId) {
        Module module = moduleRepository.findByIdAndCourseId(moduleId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Módulo não encontrado para esse curso"));
        return moduleMapper.toDTO(module);
    }

    @Override
    public List<ModuleResponseDTO> findAllByCourseId(UUID courseId) {
        List<Module> modules = moduleRepository.findAllByCourseId(courseId);
        return moduleMapper.toDTOList(modules);
    }

    @Override
    @Transactional
    public ModuleResponseDTO update(UUID moduleId, UUID courseId, ModuleRequestDTO dto){
        Module module = moduleRepository.findByIdAndCourseId(moduleId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Módulo não encontrado para esse curso"));
        moduleMapper.updateModuleFromDTO(dto, module);
        return moduleMapper.toDTO(moduleRepository.save(module));

    }

    @Override
    @Transactional
    public void delete(UUID moduleId, UUID courseId) {
        Module module = moduleRepository.findByIdAndCourseId(moduleId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Módulo não encontrado para esse curso"));
        moduleRepository.delete(module);
    }
}
