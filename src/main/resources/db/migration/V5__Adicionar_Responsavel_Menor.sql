ALTER TABLE voluntarios ADD COLUMN menor_idade BOOLEAN DEFAULT FALSE;
ALTER TABLE voluntarios ADD COLUMN nome_responsavel VARCHAR(255);
ALTER TABLE voluntarios ADD COLUMN cpf_responsavel VARCHAR(20);
ALTER TABLE voluntarios ADD COLUMN email_responsavel VARCHAR(255);
ALTER TABLE voluntarios ADD COLUMN telefone_responsavel VARCHAR(20);