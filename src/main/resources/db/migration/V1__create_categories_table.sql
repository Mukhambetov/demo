CREATE TABLE categories
(
    id           SERIAL PRIMARY KEY,
    default_name VARCHAR(255) NOT NULL
);

CREATE TABLE category_relations
(
    parent_id INT NOT NULL,
    child_id  INT NOT NULL,
    PRIMARY KEY (parent_id, child_id),
    FOREIGN KEY (parent_id) REFERENCES categories (id),
    FOREIGN KEY (child_id) REFERENCES categories (id)
);

CREATE TABLE category_translations
(
    id              SERIAL PRIMARY KEY,
    category_id     INTEGER REFERENCES categories (id) ON DELETE CASCADE,
    language        VARCHAR(2)   NOT NULL,
    translated_name VARCHAR(255) NOT NULL
);

create unique index category_translations_category_id_language_uindex
    on category_translations (category_id, language);



-- Inserting some categories in Russian
INSERT INTO categories (default_name) VALUES ('Электроника'), ('Книги'), ('Одежда'), ('Красота'), ('Спорт'), ('Походы'), ('Дом'), ('Кухня'), ('Игрушки'),
                                             ('Авто'), ('Сад'), ('Офис'), ('Музыка'), ('Животные'), ('Искусство'), ('Фильмы'), ('Обувь'), ('Сумки'), ('Ювелирные изделия'), ('Мебель');

-- Get the IDs of the categories we just inserted
-- These might be different in your database, please replace with your actual IDs
WITH category_ids AS (
    SELECT id, default_name FROM categories WHERE default_name IN ('Электроника', 'Книги', 'Одежда', 'Красота', 'Спорт', 'Походы', 'Дом', 'Кухня', 'Игрушки',
                                                     'Авто', 'Сад', 'Офис', 'Музыка', 'Животные', 'Искусство', 'Фильмы', 'Обувь', 'Сумки', 'Ювелирные изделия', 'Мебель')
)
-- Inserting some category translations
INSERT INTO category_translations (category_id, language, translated_name)
SELECT id, 'en', default_name || ' in English' FROM category_ids;

-- Inserting some parent-child relations
INSERT INTO category_relations (parent_id, child_id)
VALUES
    ((SELECT id FROM categories WHERE default_name = 'Электроника'), (SELECT id FROM categories WHERE default_name = 'Книги')),
    ((SELECT id FROM categories WHERE default_name = 'Одежда'), (SELECT id FROM categories WHERE default_name = 'Красота')),
    ((SELECT id FROM categories WHERE default_name = 'Спорт'), (SELECT id FROM categories WHERE default_name = 'Походы')),
    ((SELECT id FROM categories WHERE default_name = 'Дом'), (SELECT id FROM categories WHERE default_name = 'Кухня')),
    ((SELECT id FROM categories WHERE default_name = 'Игрушки'), (SELECT id FROM categories WHERE default_name = 'Электроника')),
    ((SELECT id FROM categories WHERE default_name = 'Авто'), (SELECT id FROM categories WHERE default_name = 'Сад')),
    ((SELECT id FROM categories WHERE default_name = 'Офис'), (SELECT id FROM categories WHERE default_name = 'Музыка')),
    ((SELECT id FROM categories WHERE default_name = 'Животные'), (SELECT id FROM categories WHERE default_name = 'Искусство')),
    ((SELECT id FROM categories WHERE default_name = 'Фильмы'), (SELECT id FROM categories WHERE default_name = 'Обувь')),
    ((SELECT id FROM categories WHERE default_name = 'Сумки'), (SELECT id FROM categories WHERE default_name = 'Ювелирные изделия')),
    ((SELECT id FROM categories WHERE default_name = 'Мебель'), (SELECT id FROM categories WHERE default_name = 'Электроника'));
