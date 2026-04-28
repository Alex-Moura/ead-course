package br.com.devalex.course.service;

import br.com.devalex.course.dtos.course.CourseRequestDTO;
import br.com.devalex.course.dtos.course.CourseResponseDTO;

import java.util.UUID;

public interface CourseService {
    CourseResponseDTO save(CourseRequestDTO dto);
}
