package br.com.devalex.course.repository;

import br.com.devalex.course.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID>, JpaSpecificationExecutor<Lesson> {
    Optional<Lesson> findByIdAndModuleId(UUID lessonId, UUID moduleId);
    List<Lesson> findAllByModuleId(UUID moduleId);
}
