package br.com.devalex.course.service;

import br.com.devalex.course.dtos.course.CourseRequestDTO;
import br.com.devalex.course.dtos.course.CourseResponseDTO;
import br.com.devalex.course.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface CourseService {
    CourseResponseDTO save(CourseRequestDTO dto);
    void delete(UUID id);
    Page<CourseResponseDTO> findAll(Specification<Course> spec, Pageable pageable);
    CourseResponseDTO findById(UUID id);
    CourseResponseDTO update(UUID id, CourseRequestDTO dto);
}
