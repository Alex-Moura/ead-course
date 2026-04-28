package br.com.devalex.course.dtos.course;

import br.com.devalex.course.enums.CourseLevel;
import br.com.devalex.course.enums.CourseStatus;

import java.util.UUID;

public record CourseResponseDTO(
        String name,
        String imgUrl,
        CourseStatus courseStatus,
        CourseLevel courseLevel,
        UUID userInstructor,
        String description
){}
