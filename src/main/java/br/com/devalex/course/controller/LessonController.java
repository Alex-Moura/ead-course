package br.com.devalex.course.controller;


import br.com.devalex.course.dtos.lesson.LessonRequestDTO;
import br.com.devalex.course.dtos.lesson.LessonResponseDTO;
import br.com.devalex.course.service.LessonService;
import br.com.devalex.course.specification.SpecificationTemplate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/courses/{courseId}/modules/{moduleId}")
public class LessonController {

    private final LessonService lessonService;

    @PostMapping("/lessons")
    public ResponseEntity<LessonResponseDTO> save(
            @PathVariable UUID courseId,
            @PathVariable UUID moduleId,
            @RequestBody @Valid LessonRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lessonService.save(dto, courseId, moduleId));
    }

    @GetMapping("/lessons")
    public ResponseEntity<Page<LessonResponseDTO>> findAll(
            @PathVariable UUID courseId,
            @PathVariable UUID moduleId,
            SpecificationTemplate.LessonSpec spec,
            @PageableDefault(page = 0, size = 10, sort = "title",
                    direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(lessonService.findAllByModuleId(courseId, moduleId, spec, pageable));
    }
    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<LessonResponseDTO> findById(
            @PathVariable UUID courseId,
            @PathVariable UUID moduleId,
            @PathVariable UUID lessonId) {
        return ResponseEntity.ok(lessonService.findById(lessonId, courseId, moduleId));
    }

    @PutMapping("/lessons/{lessonId}")
    public ResponseEntity<LessonResponseDTO> update(
            @PathVariable UUID courseId,
            @PathVariable UUID moduleId,
            @PathVariable UUID lessonId,
            @RequestBody @Valid LessonRequestDTO dto) {
        return ResponseEntity.ok(lessonService.update(lessonId, courseId, moduleId, dto));
    }

    @DeleteMapping("/lessons/{lessonId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID courseId,
            @PathVariable UUID moduleId,
            @PathVariable UUID lessonId) {
        lessonService.delete(lessonId, courseId, moduleId);
        return ResponseEntity.noContent().build();
    }
}