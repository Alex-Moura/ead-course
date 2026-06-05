package br.com.devalex.course.dtos.module;

import br.com.devalex.course.dtos.lessons.LessonResponseDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ModuleResponseDTO(
        UUID id,
        String title,
        String description,
//        List<LessonResponseDTO> lessons,
        LocalDateTime createdAt
) {
}
