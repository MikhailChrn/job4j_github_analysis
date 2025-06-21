CREATE TABLE commits (
    id            SERIAL PRIMARY KEY,
    message       TEXT,
    author        TEXT,
    date          TIMESTAMP,
    repository_id INT   NOT NULL   REFERENCES repositories(id)
);