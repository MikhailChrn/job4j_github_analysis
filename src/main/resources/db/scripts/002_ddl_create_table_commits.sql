CREATE TABLE commits (
    id          SERIAL PRIMARY KEY,
    html_url    TEXT   UNIQUE,
    message     TEXT,
    author_name TEXT,
    author_date TIMESTAMP,
    repo_id     INT    NOT NULL REFERENCES  repos(id)
);