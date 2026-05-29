package br.com.devalex.course.dtos.course;

import br.com.devalex.course.enums.CourseLevel;
import br.com.devalex.course.enums.CourseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CourseRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres")
        String name,

        @Pattern(
            regexp = "^(http|https)://.*$",
            message = "URL da imagem deve ser válida"
        )
        @NotBlank(message = "Imagem é obrigatório")
        String imgUrl,

        @NotNull(message = "Status do curso é não pode ser nulo")
        CourseStatus courseStatus,

        @NotNull(message = "Nível do curso é não pode ser nulo")
        CourseLevel courseLevel,

        @NotNull(message = "Instrutor do curso é obrigatorio")
        UUID userInstructor,

        @NotBlank(message = "Descrição é obrigatória")
        String description
) {
}
