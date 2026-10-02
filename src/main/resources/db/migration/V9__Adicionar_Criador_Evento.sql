ALTER TABLE eventos ADD COLUMN criador_id BIGINT;
ALTER TABLE eventos ADD CONSTRAINT fk_eventos_criador FOREIGN KEY (criador_id) REFERENCES usuarios(id);
