package br.com.devalex.course.common;


import br.com.devalex.course.dtos.course.CourseRequestDTO;
import br.com.devalex.course.dtos.course.CourseResponseDTO;
import br.com.devalex.course.dtos.lesson.LessonRequestDTO;
import br.com.devalex.course.dtos.lesson.LessonResponseDTO;
import br.com.devalex.course.dtos.module.ModuleRequestDTO;
import br.com.devalex.course.dtos.module.ModuleResponseDTO;
import br.com.devalex.course.enums.CourseLevel;
import br.com.devalex.course.enums.CourseStatus;
import br.com.devalex.course.model.Course;
import br.com.devalex.course.model.Lesson;
import br.com.devalex.course.model.Module;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.UUID;

public class TestFixtures {
    public static final UUID COURSE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID MODULE_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final UUID LESSON_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    public static final UUID INSTRUCTOR_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    public static CourseRequestDTO courseRequest() {
        return new CourseRequestDTO(
                "Spring Boot na Prática",
                "https://img.example.com/spring.jpg",
                CourseStatus.INPROGRESS,
                CourseLevel.BEGINNER,
                INSTRUCTOR_ID,
                "Aprenda Spring Boot do zero ao avançado"
        );
    }

    public static CourseResponseDTO courseResponse() {
        return new CourseResponseDTO(
                COURSE_ID,
                "Spring Boot na Prática",
                "https://img.example.com/spring.jpg",
                CourseStatus.INPROGRESS,
                CourseLevel.BEGINNER,
                INSTRUCTOR_ID,
                "Aprenda Spring Boot do zero ao avançado"
        );
    }

    public static Course courseEntity() {
        Course c = new Course();
        ReflectionTestUtils.setField(c, "id", COURSE_ID);
        c.setName("Spring Boot na Prática");
        c.setImgUrl("https://img.example.com/spring.jpg");
        c.setCourseStatus(CourseStatus.INPROGRESS);
        c.setCourseLevel(CourseLevel.BEGINNER);
        c.setUserInstructor(INSTRUCTOR_ID);
        c.setDescription("Aprenda Spring Boot do zero ao avançado");
        return c;
    }

    public static ModuleRequestDTO moduleRequest() {
        return new ModuleRequestDTO(
                "Módulo 1 - Fundamentos",
                "Conceitos básicos de Spring"
        );
    }

    public static ModuleResponseDTO moduleResponse() {
        return new ModuleResponseDTO(
                MODULE_ID,
                "Módulo 1 - Fundamentos",
                "Conceitos básicos de Spring",
                LocalDateTime.of(2024, 1, 15, 10, 0)
        );
    }

    public static Module moduleEntity() {
        Module m = new Module();
        ReflectionTestUtils.setField(m, "id", MODULE_ID);
        m.setTitle("Módulo 1 - Fundamentos");
        m.setDescription("Conceitos básicos de Spring");
        m.setCourse(courseEntity());
        return m;
    }

    public static LessonRequestDTO lessonRequest() {
        return new LessonRequestDTO(
                "Aula 1 - O que é Spring?",
                "Introdução ao framework",
                "https://video.example.com/aula1.mp4"
        );
    }

    public static LessonResponseDTO lessonResponse() {
        return new LessonResponseDTO(
                LESSON_ID,
                "Aula 1 - O que é Spring?",
                "Introdução ao framework",
                "https://video.example.com/aula1.mp4"
        );
    }

    public static Lesson lessonEntity() {
        Lesson l = new Lesson();
        ReflectionTestUtils.setField(l, "id", LESSON_ID);
        l.setTitle("Aula 1 - O que é Spring?");
        l.setDescription("Introdução ao framework");
        l.setVideoUrl("https://video.example.com/aula1.mp4");
        l.setModule(moduleEntity());
        return l;
    }

}
