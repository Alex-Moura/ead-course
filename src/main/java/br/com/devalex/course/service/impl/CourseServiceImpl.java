package br.com.devalex.course.service.impl;

import br.com.devalex.course.dtos.course.CourseRequestDTO;
import br.com.devalex.course.dtos.course.CourseResponseDTO;
import br.com.devalex.course.mapper.CourseMapper;
import br.com.devalex.course.model.Course;
import br.com.devalex.course.repository.CourseRepository;
import br.com.devalex.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
