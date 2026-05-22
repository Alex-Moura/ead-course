package br.com.devalex.course.service;

import br.com.devalex.course.dtos.lessons.LessonRequestDTO;
import br.com.devalex.course.dtos.lessons.LessonResponseDTO;

import java.util.UUID;

public interface LessonService {
    LessonResponseDTO save (LessonRequestDTO dto, UUID moduleId);
    LessonResponseDTO findById(UUID lessonId, UUID moduleId);
}
