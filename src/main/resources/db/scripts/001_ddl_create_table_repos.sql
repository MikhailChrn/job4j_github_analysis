CREATE TABLE repos (
    id          SERIAL PRIMARY KEY,
    html_url    TEXT,
    full_name   TEXT,
    created_at  TIMESTAMP,
    description TEXT
);