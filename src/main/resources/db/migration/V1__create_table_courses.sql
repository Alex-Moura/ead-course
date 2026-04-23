CREATE TABLE courses (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(255) NOT NULL,
    img_url VARCHAR(255) NOT NULL,
    course_status VARCHAR(255) NOT NULL,
    course_level VARCHAR(255) NOT NULL,
    user_instructor UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
)