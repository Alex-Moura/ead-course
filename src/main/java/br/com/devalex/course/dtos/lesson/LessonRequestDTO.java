package br.com.devalex.course.dtos.lesson;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LessonRequestDTO(
        @NotBlank(message = "Título é obrigatório")
        @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres")
        String title,

        @NotBlank(message = "Descrição é obrigatório")
        String description,

        @NotBlank(message = "Video é obrigatório")
        String videoUrl
) {
}
