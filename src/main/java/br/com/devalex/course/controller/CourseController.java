package br.com.devalex.course.controller;

import br.com.devalex.course.dtos.course.CourseRequestDTO;
import br.com.devalex.course.dtos.course.CourseResponseDTO;
import br.com.devalex.course.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseResponseDTO> save(@RequestBody @Valid CourseRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.save(dto));
    }
}
