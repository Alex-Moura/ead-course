package br.com.devalex.course.controller;

import br.com.devalex.course.dtos.course.CourseRequestDTO;
import br.com.devalex.course.dtos.course.CourseResponseDTO;
import br.com.devalex.course.service.CourseService;
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
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseResponseDTO> save(@RequestBody @Valid CourseRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.save(dto));
    }

    @GetMapping
    public ResponseEntity<Page<CourseResponseDTO>> findAll(
            SpecificationTemplate.CourseSpec spec,
            @PageableDefault(page = 0, size = 10, sort = "id",
                    direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.ok(courseService.findAll(spec, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> findById(@PathVariable UUID id){
        return ResponseEntity.ok(courseService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid CourseRequestDTO dto){
        return ResponseEntity.ok(courseService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
