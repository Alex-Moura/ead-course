package br.com.devalex.course.dtos.module;

import br.com.devalex.course.dtos.course.CourseResponseDTO;
import br.com.devalex.course.model.Course;
import br.com.devalex.course.model.Lesson;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ModuleResponseDTO(
        UUID id,
        String title,
        String description,
        CourseResponseDTO course,
        List<Lesson> lessons,
        LocalDateTime createdAt
) {
}
