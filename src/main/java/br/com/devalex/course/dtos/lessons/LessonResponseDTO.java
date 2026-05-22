package br.com.devalex.course.dtos.lessons;

import java.util.UUID;

public record LessonResponseDTO(
        UUID id,
        String title,
        String description,
        String videoUrl
) {
}
