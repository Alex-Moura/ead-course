package br.com.devalex.course.mapper;

import br.com.devalex.course.dtos.module.ModuleRequestDTO;
import br.com.devalex.course.dtos.module.ModuleResponseDTO;
import br.com.devalex.course.model.Module;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ModuleMapper {
    ModuleResponseDTO toDTO(Module module);
    Module toEntity(ModuleRequestDTO dto);
    List<ModuleResponseDTO> toDTOList(List<Module> modules);
    void updateCourseFromDTO(ModuleRequestDTO dto,
                             @MappingTarget Module module);
}
