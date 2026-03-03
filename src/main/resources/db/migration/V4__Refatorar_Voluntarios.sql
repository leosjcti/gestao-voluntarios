ALTER TABLE voluntarios RENAME COLUMN manual_entregue TO antecedentes_analisados;

ALTER TABLE voluntarios ALTER COLUMN antecedentes_analisados SET DEFAULT FALSE;

ALTER TABLE voluntarios DROP COLUMN IF EXISTS data_integracao;
ALTER TABLE voluntarios DROP COLUMN IF EXISTS lider_integracao;
