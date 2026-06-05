package br.com.devalex.course.service.impl;

import br.com.devalex.course.dtos.lessons.LessonRequestDTO;
import br.com.devalex.course.dtos.lessons.LessonResponseDTO;
import br.com.devalex.course.exceptions.ErrorMessages;
import br.com.devalex.course.exceptions.custom.ResourceNotFoundException;
import br.com.devalex.course.mapper.LessonMapper;
import br.com.devalex.course.model.Lesson;
import br.com.devalex.course.model.Module;
import br.com.devalex.course.repository.LessonRepository;
import br.com.devalex.course.repository.ModuleRepository;
import br.com.devalex.course.service.LessonService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final LessonMapper lessonMapper;

    @Override
    @Transactional
    public LessonResponseDTO save(LessonRequestDTO dto, UUID courseId, UUID moduleId) {
        Module module = moduleRepository.findByIdAndCourseId(moduleId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.MODULE_NOT_IN_COURSE, moduleId, courseId)));

        Lesson lesson = lessonMapper.toEntity(dto);
        lesson.setModule(module);
        return lessonMapper.toDTO(lessonRepository.save(lesson));
    }

    @Override
    public LessonResponseDTO findById(UUID lessonId, UUID courseId, UUID moduleId) {
        moduleRepository.findByIdAndCourseId(moduleId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.MODULE_NOT_IN_COURSE, moduleId, courseId)));

        return lessonRepository.findByIdAndModuleId(lessonId, moduleId)
                .map(lessonMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.LESSON_NOT_FOUND,lessonId)));
    }

    @Override
    public List<LessonResponseDTO> findAllByModuleId(UUID courseId, UUID moduleId) {
        moduleRepository.findByIdAndCourseId(moduleId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.MODULE_NOT_IN_COURSE, moduleId, courseId)));

        return lessonMapper.toDTOList(lessonRepository.findAllByModuleId(moduleId));
    }

    @Override
    @Transactional
    public LessonResponseDTO update(UUID lessonId, UUID courseId, UUID moduleId, LessonRequestDTO dto) {
        moduleRepository.findByIdAndCourseId(moduleId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.MODULE_NOT_IN_COURSE, moduleId, courseId)));

        Lesson lesson = lessonRepository.findByIdAndModuleId(lessonId, moduleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.LESSON_NOT_FOUND,lessonId)));
        lessonMapper.updateLessonFromDTO(dto, lesson);
        return lessonMapper.toDTO(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    public void delete(UUID lessonId, UUID courseId, UUID moduleId) {
        moduleRepository.findByIdAndCourseId(moduleId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.MODULE_NOT_IN_COURSE, moduleId, courseId)));

        Lesson lesson = lessonRepository.findByIdAndModuleId(lessonId, moduleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.LESSON_NOT_FOUND,lessonId)));
        lessonRepository.delete(lesson);
    }
}