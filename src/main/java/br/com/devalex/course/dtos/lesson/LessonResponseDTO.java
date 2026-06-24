package br.com.devalex.course.dtos.lesson;

import java.util.UUID;

public record LessonResponseDTO(
        UUID id,
        String title,
        String description,
        String videoUrl
) {
}
