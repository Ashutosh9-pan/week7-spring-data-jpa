ALTER TABLE categories
    ADD COLUMN parent_category_id BIGINT;
ALTER TABLE categories
    ADD CONSTRAINT fk_category_parent
        FOREIGN KEY (parent_category_id)
        REFERENCES categories(id);
CREATE INDEX idx_category_parent
    ON categories(parent_category_id);
