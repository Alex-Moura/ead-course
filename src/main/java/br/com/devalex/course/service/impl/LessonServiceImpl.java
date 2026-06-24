package br.com.devalex.course.service.impl;

import br.com.devalex.course.dtos.lesson.LessonRequestDTO;
import br.com.devalex.course.dtos.lesson.LessonResponseDTO;
import br.com.devalex.course.exceptions.ErrorMessages;
import br.com.devalex.course.exceptions.custom.ResourceNotFoundException;
import br.com.devalex.course.mapper.LessonMapper;
import br.com.devalex.course.model.Lesson;
import br.com.devalex.course.model.Module;
import br.com.devalex.course.repository.LessonRepository;
import br.com.devalex.course.repository.ModuleRepository;
import br.com.devalex.course.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if (!moduleRepository.existsByIdAndCourseId(moduleId, courseId)) {
            throw new ResourceNotFoundException(
                    String.format(ErrorMessages.MODULE_NOT_IN_COURSE, moduleId, courseId));
        }
        return lessonRepository.findByIdAndModuleId(lessonId, moduleId)
                .map(lessonMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.LESSON_NOT_FOUND,lessonId)));
    }

    @Override
    public Page<LessonResponseDTO> findAllByModuleId(UUID courseId, UUID moduleId, Specification<Lesson> spec, Pageable pageable) {
        if (!moduleRepository.existsByIdAndCourseId(moduleId, courseId)) {
            throw new ResourceNotFoundException(
                    String.format(ErrorMessages.MODULE_NOT_IN_COURSE, moduleId, courseId));
        }

        Specification<Lesson> byModuleId = (root, query, cb) ->
                cb.equal(root.get("module").get("id"), moduleId);

        Specification<Lesson> combined = Specification.where(byModuleId).and(spec);

        return lessonRepository.findAll(combined, pageable)
                .map(lessonMapper::toDTO);
    }

    @Override
    @Transactional
    public LessonResponseDTO update(UUID lessonId, UUID courseId, UUID moduleId, LessonRequestDTO dto) {
        if (!moduleRepository.existsByIdAndCourseId(moduleId, courseId)) {
            throw new ResourceNotFoundException(
                    String.format(ErrorMessages.MODULE_NOT_IN_COURSE, moduleId, courseId));
        }
        Lesson lesson = lessonRepository.findByIdAndModuleId(lessonId, moduleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.LESSON_NOT_FOUND,lessonId)));
        lessonMapper.updateLessonFromDTO(dto, lesson);
        return lessonMapper.toDTO(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    public void delete(UUID lessonId, UUID courseId, UUID moduleId) {
        if (!moduleRepository.existsByIdAndCourseId(moduleId, courseId)) {
            throw new ResourceNotFoundException(
                    String.format(ErrorMessages.MODULE_NOT_IN_COURSE, moduleId, courseId));
        }
        Lesson lesson = lessonRepository.findByIdAndModuleId(lessonId, moduleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.LESSON_NOT_FOUND,lessonId)));
        lessonRepository.delete(lesson);
    }
}