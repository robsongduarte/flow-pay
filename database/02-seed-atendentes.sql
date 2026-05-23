-- Seed inicial de atendentes.
-- Script idempotente para evitar duplicacao de nomes.

INSERT INTO atendentes (nome, time_atendimento, ativo, criado_em, version)
SELECT 'Ana Cartoes', 'CARTOES', TRUE, NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM atendentes WHERE nome = 'Ana Cartoes');

INSERT INTO atendentes (nome, time_atendimento, ativo, criado_em, version)
SELECT 'Bruno Cartoes', 'CARTOES', TRUE, NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM atendentes WHERE nome = 'Bruno Cartoes');

INSERT INTO atendentes (nome, time_atendimento, ativo, criado_em, version)
SELECT 'Carla Emprestimos', 'EMPRESTIMOS', TRUE, NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM atendentes WHERE nome = 'Carla Emprestimos');

INSERT INTO atendentes (nome, time_atendimento, ativo, criado_em, version)
SELECT 'Diego Emprestimos', 'EMPRESTIMOS', TRUE, NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM atendentes WHERE nome = 'Diego Emprestimos');

INSERT INTO atendentes (nome, time_atendimento, ativo, criado_em, version)
SELECT 'Eva Outros', 'OUTROS_ASSUNTOS', TRUE, NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM atendentes WHERE nome = 'Eva Outros');

INSERT INTO atendentes (nome, time_atendimento, ativo, criado_em, version)
SELECT 'Fabio Outros', 'OUTROS_ASSUNTOS', TRUE, NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM atendentes WHERE nome = 'Fabio Outros');
