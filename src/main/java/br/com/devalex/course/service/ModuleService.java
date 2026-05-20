package br.com.devalex.course.service;

import br.com.devalex.course.dtos.module.ModuleRequestDTO;
import br.com.devalex.course.dtos.module.ModuleResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ModuleService {
    ModuleResponseDTO save(ModuleRequestDTO dto, UUID courseId);
    ModuleResponseDTO findById(UUID moduleId, UUID courseId);
    List<ModuleResponseDTO> findAllByCourseId(UUID courseId);
    ModuleResponseDTO update(UUID moduleId, UUID courseId, ModuleRequestDTO dto);
    void delete (UUID moduleId, UUID courseId);
}
