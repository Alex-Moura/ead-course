package br.com.devalex.course.service;

import br.com.devalex.course.dtos.lesson.LessonRequestDTO;
import br.com.devalex.course.dtos.lesson.LessonResponseDTO;
import br.com.devalex.course.model.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface LessonService {
    LessonResponseDTO save(LessonRequestDTO dto, UUID courseId, UUID moduleId);
    LessonResponseDTO findById(UUID lessonId, UUID courseId, UUID moduleId);
    Page<LessonResponseDTO> findAllByModuleId(UUID courseId, UUID moduleId,
                                              Specification<Lesson> spec,
                                              Pageable pageable);
    LessonResponseDTO update(UUID lessonId, UUID courseId, UUID moduleId, LessonRequestDTO dto);
    void delete(UUID lessonId, UUID courseId, UUID moduleId);
}
