package br.com.devalex.course.specification;

import br.com.devalex.course.model.Course;
import br.com.devalex.course.model.Lesson;
import br.com.devalex.course.model.Module;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationTemplate {
    @And({
            @Spec(path="courseLevel", spec= Equal.class),
            @Spec(path = "courseStatus", spec = Equal.class),
            @Spec(path = "name", spec = Like.class)
    })
    public interface CourseSpec extends Specification<Course> {}


    @Spec(path="title", spec= Equal.class)
    public interface ModuleSpec extends Specification<Module> {}

    @Spec(path="title", spec= Equal.class)
    public interface LessonSpec extends Specification<Lesson> {}

}

