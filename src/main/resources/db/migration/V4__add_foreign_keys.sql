ALTER TABLE modules
ADD COLUMN course_id UUID NOT NULL;

ALTER TABLE modules
ADD CONSTRAINT fk_module_course
FOREIGN KEY (course_id)
REFERENCES courses(id);

ALTER TABLE lessons
ADD COLUMN module_id UUID NOT NULL;

ALTER TABLE lessons
ADD CONSTRAINT fk_lesson_module
FOREIGN KEY (module_id)
REFERENCES modules(id);