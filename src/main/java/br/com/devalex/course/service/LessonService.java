package br.com.devalex.course.service;

import br.com.devalex.course.dtos.lessons.LessonRequestDTO;
import br.com.devalex.course.dtos.lessons.LessonResponseDTO;

import java.util.List;
import java.util.UUID;

public interface LessonService {
    LessonResponseDTO save(LessonRequestDTO dto, UUID courseId, UUID moduleId);
    LessonResponseDTO findById(UUID lessonId, UUID courseId, UUID moduleId);
    List<LessonResponseDTO> findAllByModuleId(UUID courseId, UUID moduleId);
    LessonResponseDTO update(UUID lessonId, UUID courseId, UUID moduleId, LessonRequestDTO dto);
    void delete(UUID lessonId, UUID courseId, UUID moduleId);
}
