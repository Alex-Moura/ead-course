package br.com.devalex.course.controller;


import br.com.devalex.course.dtos.module.ModuleRequestDTO;
import br.com.devalex.course.dtos.module.ModuleResponseDTO;
import br.com.devalex.course.service.ModuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/courses/{courseId}")
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping("/modules")
    public ResponseEntity<ModuleResponseDTO> save(
            @PathVariable UUID courseId,
            @RequestBody @Valid ModuleRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleService.save(dto, courseId));
    }

    @GetMapping("/modules/{moduleId}")
    public ResponseEntity<ModuleResponseDTO> findById(@PathVariable UUID courseId,
                                                      @PathVariable UUID moduleId){
        return ResponseEntity.ok(moduleService.findById(moduleId, courseId));
    }

    @GetMapping("/modules")
    public ResponseEntity<List<ModuleResponseDTO>> findAllByCourseId(@PathVariable UUID courseId){
        return ResponseEntity.ok(moduleService.findAllByCourseId(courseId));
    }

    @PutMapping("/modules/{moduleId}")
    public ResponseEntity<ModuleResponseDTO> update(@PathVariable UUID courseId,
                                                    @PathVariable UUID moduleId,
                                                    @RequestBody @Valid ModuleRequestDTO dto){
        return ResponseEntity.ok(moduleService.update(moduleId, courseId, dto));
    }

    @DeleteMapping("/modules/{moduleId}")
    public ResponseEntity<Void> delete(@PathVariable UUID courseId,
                                       @PathVariable UUID moduleId){
        moduleService.delete(moduleId, courseId);
        return ResponseEntity.noContent().build();
    }
}
