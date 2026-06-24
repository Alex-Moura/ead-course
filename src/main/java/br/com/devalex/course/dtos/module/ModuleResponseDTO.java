package br.com.devalex.course.dtos.module;

import java.time.LocalDateTime;
import java.util.UUID;

public record ModuleResponseDTO(
        UUID id,
        String title,
        String description,
        LocalDateTime createdAt
) {
}
