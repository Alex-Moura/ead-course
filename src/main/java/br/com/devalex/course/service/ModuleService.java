package br.com.devalex.course.service;

import br.com.devalex.course.dtos.module.ModuleRequestDTO;
import br.com.devalex.course.dtos.module.ModuleResponseDTO;
import br.com.devalex.course.model.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface ModuleService {
    ModuleResponseDTO save(ModuleRequestDTO dto, UUID courseId);
    ModuleResponseDTO findById(UUID moduleId, UUID courseId);
    Page<ModuleResponseDTO> findAllByCourseId(UUID courseId, Specification<Module> spec, Pageable pageable);
    ModuleResponseDTO update(UUID moduleId, UUID courseId, ModuleRequestDTO dto);
    void delete (UUID moduleId, UUID courseId);
}
