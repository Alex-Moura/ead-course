CREATE TABLE lessons (
    id UUID PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(255) NOT NULL,
    video_url VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
)