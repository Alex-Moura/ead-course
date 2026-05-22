package br.com.devalex.course.service.impl;

import br.com.devalex.course.dtos.lessons.LessonRequestDTO;
import br.com.devalex.course.dtos.lessons.LessonResponseDTO;
import br.com.devalex.course.exceptions.custom.ResourceNotFoundException;
import br.com.devalex.course.mapper.LessonMapper;
import br.com.devalex.course.model.Lesson;
import br.com.devalex.course.model.Module;
import br.com.devalex.course.repository.LessonRepository;
import br.com.devalex.course.repository.ModuleRepository;
import br.com.devalex.course.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final LessonMapper lessonMapper;

    @Override
    public LessonResponseDTO save(LessonRequestDTO dto, UUID moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Modulo com id: " + moduleId + " não encotrado"));
        Lesson lesson = lessonMapper.toEntity(dto);
        lesson.setModule(module);
        return lessonMapper.toDTO(lessonRepository.save(lesson));
    }

    @Override
    public LessonResponseDTO findById(UUID lessonId, UUID moduleId) {
        Lesson lesson = lessonRepository.findByIdAndModuleId(lessonId, moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Lição não encontrada para esse módulo"));
        return lessonMapper.toDTO(lesson);
    }

    @Override
    public List<LessonResponseDTO> findAllByModuleId(UUID moduleId) {
        List<Lesson> lessons = lessonRepository.findAllByModuleId(moduleId);
        return lessonMapper.toDTOList(lessons);
    }
}
