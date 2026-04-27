ALTER TABLE modules
DROP CONSTRAINT fk_module_course;

ALTER TABLE modules
ADD CONSTRAINT fk_module_course
FOREIGN KEY (course_id)
REFERENCES courses(id)
ON DELETE CASCADE;

ALTER TABLE lessons
DROP CONSTRAINT fk_lesson_module;

ALTER TABLE lessons
ADD CONSTRAINT fk_lesson_module
FOREIGN KEY (module_id)
REFERENCES modules(id)
ON DELETE CASCADE;