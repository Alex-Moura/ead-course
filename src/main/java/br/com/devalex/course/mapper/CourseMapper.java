package br.com.devalex.course.mapper;

import br.com.devalex.course.dtos.course.CourseRequestDTO;
import br.com.devalex.course.dtos.course.CourseResponseDTO;
import br.com.devalex.course.model.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    Course toEntity(CourseRequestDTO dto);
    CourseResponseDTO toDTO(Course course);
}
