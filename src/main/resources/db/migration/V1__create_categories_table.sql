CREATE TABLE categories
(
    id        SERIAL PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    parent_id INTEGER      REFERENCES categories (id) ON DELETE SET NULL
);
