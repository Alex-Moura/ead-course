package br.com.devalex.course.controller;


import br.com.devalex.course.dtos.lessons.LessonRequestDTO;
import br.com.devalex.course.dtos.lessons.LessonResponseDTO;
import br.com.devalex.course.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/modules/{moduleId}")
public class LessonController {
    private final LessonService lessonService;

    @PostMapping("/lessons")
    public ResponseEntity<LessonResponseDTO> save(@PathVariable UUID moduleId,
                                                  @RequestBody @Valid LessonRequestDTO dto){
       return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.save(dto, moduleId));
    }
}
