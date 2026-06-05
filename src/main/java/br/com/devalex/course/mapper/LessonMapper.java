package br.com.devalex.course.mapper;

import br.com.devalex.course.dtos.lessons.LessonRequestDTO;
import br.com.devalex.course.dtos.lessons.LessonResponseDTO;
import br.com.devalex.course.model.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    LessonResponseDTO toDTO(Lesson lesson);
    Lesson toEntity(LessonRequestDTO dto);
    List<LessonResponseDTO> toDTOList(List<Lesson> lessons);
    void updateLessonFromDTO(LessonRequestDTO dto,
                             @MappingTarget Lesson lesson);
}
