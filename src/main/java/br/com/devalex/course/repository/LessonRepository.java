package br.com.devalex.course.repository;

import br.com.devalex.course.model.Lesson;
import br.com.devalex.course.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    Optional<Lesson> findByIdAndModuleId(UUID lessonId, UUID moduleId);
}
