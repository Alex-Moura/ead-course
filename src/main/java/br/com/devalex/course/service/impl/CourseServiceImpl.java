package br.com.devalex.course.service.impl;

import br.com.devalex.course.dtos.course.CourseRequestDTO;
import br.com.devalex.course.dtos.course.CourseResponseDTO;
import br.com.devalex.course.exceptions.custom.ResourceNotFoundException;
import br.com.devalex.course.mapper.CourseMapper;
import br.com.devalex.course.model.Course;
import br.com.devalex.course.repository.CourseRepository;
import br.com.devalex.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Override
    public CourseResponseDTO save(CourseRequestDTO dto) {
        Course course = courseMapper.toEntity(dto);
        return  courseMapper.toDTO(courseRepository.save(course));
    }

    @Override
    public void delete(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso com id: " + id + " não encontrado"));
        courseRepository.delete(course);
    }

    @Override
    public CourseResponseDTO findById(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Curso com id: " + id + " não encontrado"));
        return courseMapper.toDTO(course);
    }

    @Override
    public CourseResponseDTO update(UUID id, CourseRequestDTO dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso com id: " + id + " não encontrado"));
        courseMapper.updateCourseFromDTO(dto, course);
        return courseMapper.toDTO(courseRepository.save(course));
    }

    @Override
    public List<CourseResponseDTO> findAll() {
        List<Course> courses = courseRepository.findAll();
        return courseMapper.toDTOList(courses);
    }
}
