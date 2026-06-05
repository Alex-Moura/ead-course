package br.com.devalex.course.service;

import br.com.devalex.course.dtos.course.CourseRequestDTO;
import br.com.devalex.course.dtos.course.CourseResponseDTO;

import java.util.List;
import java.util.UUID;

public interface CourseService {
    CourseResponseDTO save(CourseRequestDTO dto);
    void delete(UUID id);
    List<CourseResponseDTO> findAll();
    CourseResponseDTO findById(UUID id);
    CourseResponseDTO update(UUID id, CourseRequestDTO dto);
}
