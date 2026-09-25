

--EMPRESA
--01
INSERT INTO public.empresas(
	ativa, criado_em, id, cnpj, nome, email, endereco, telefone)
	VALUES (TRUE, '2026-09-22 00:00:00', 1, '02123456000157', 'HOPE SUPORTE', 'SUPORTE@HOPE.COM', 'RUA SC33 QD.67 LT.22', '62994609450');
--02
INSERT INTO public.empresas(
	ativa, criado_em, id, cnpj, nome, email, endereco, telefone)
	VALUES (TRUE, '2026-09-22 00:00:00', 2, '062369123000157', 'LUZ NOROESTE', 'LUZPOVOSNOROESTE@GMAIL.COM', 'RUA CONDE MATARAZZU', '62994609455');

-- DEPARTAMENTO
INSERT INTO public.departamentos(ativo, empresa_id, id, nome) VALUES (TRUE, 2, 1, 'LOUVOR');


--INSTRUMENTOS
INSERT INTO public.instrumentos (ativo, quantidade_escala, empresa_id, id, descricao, nome, tipo) VALUES (TRUE,1, 2, 1, 'VOZ', 'MINISTRO', 'VOZ');
INSERT INTO public.instrumentos (ativo, quantidade_escala, empresa_id, id, descricao, nome, tipo) VALUES (TRUE,4, 2, 2, 'VOZ', 'BACK-VOCAL', 'VOZ');
INSERT INTO public.instrumentos (ativo, quantidade_escala, empresa_id, id, descricao, nome, tipo) VALUES (TRUE,1, 2, 3, 'INSTRUMENTOS DE CORDAS', 'BAIXO', 'BAIXO');
INSERT INTO public.instrumentos (ativo, quantidade_escala, empresa_id, id, descricao, nome, tipo) VALUES (TRUE,1, 2, 4, 'INSTRUMENTOS DE CORDAS', 'VIOLAO', 'VIOLAO');
INSERT INTO public.instrumentos (ativo, quantidade_escala, empresa_id, id, descricao, nome, tipo) VALUES (TRUE,1, 2, 5, 'INSTRUMENTOS DE CORDAS', 'GUITARRA', 'GUITARRA');
INSERT INTO public.instrumentos (ativo, quantidade_escala, empresa_id, id, descricao, nome, tipo) VALUES (TRUE,1, 2, 6, 'TECLAS', 'TECLADO', 'TECLAS');
INSERT INTO public.instrumentos (ativo, quantidade_escala, empresa_id, id, descricao, nome, tipo) VALUES (TRUE,1, 2, 7, 'PERCURSIVO', 'BATERIA', 'BATERIA');
 

--USUARIOS
	
INSERT INTO usuarios (
    nome, 
    email, 
    senha, 
    perfil, 
    ativo, 
    empresa_id
) VALUES (
    'Super Administrador', 
    'admin@hopeescala.com', 
    '$2a$10$eACCYoNOHEqgkZdd9dWbVue79g7m8yO6p6zFv8QkQ1u1P4QkM4QCe', -- Hash BCrypt
    'SUPER_ADMIN', 
    true, 
    1
);	

UPDATE usuarios 
SET senha = '$2a$10$eACCYoNOHEqgkZdd9dWbVue79g7m8yO6p6zFv8QkQ1u1P4QkM4QCe',
    ativo = true
WHERE email = 'admin@hope.com';

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(1, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'suporte@hope.com', 'DIEURE OLIVEIRA', NULL, 'SUPER_ADMIN', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,1);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(2, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'dieure@gmail.com', 'DIEURE OLIVEIRA', NULL, 'ADMIN', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(3, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'admin1@teste.com', 'ISABELLA', NULL, 'ADMIN', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(4, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'admin2@teste.com', 'JOAO EDUARDO', NULL, 'ADMIN', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(5, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'claudio@teste.com', 'CLAUDIO', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(6, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'herica1@teste.com', 'HERICA', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(7, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'tairynne@teste.com', 'TAIRYNNE', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(8, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'rosimar@teste.com', 'ROSIMAR', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(9, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'dudu@teste.com', 'DUDU', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(10, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'eduardomk@teste.com', 'EDUARDO MARK', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(11, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'marcio@teste.com', 'MARCIO', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(12, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'lorran@teste.com', 'LORRAN', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(13, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'richard@teste.com', 'RICHARD', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(14, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'diego@teste.com', 'DIEGO', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(15, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'ludmilla@teste.com', 'LUDMILLA', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(16, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'michelle@teste.com', 'MICHELLE', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(17, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'aline@teste.com', 'ALINE', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(18, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'patricia@teste.com', 'PATRICIA', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(19, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'kamilla@teste.com', 'KAMILLA', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(20, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'sara1@teste.com', 'SARA-1', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(21, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'sara2@teste.com', 'SARA-2', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);  

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(22, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'rogerio@teste.com', 'ROGERIO', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(23, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'lucelia@teste.com', 'LUCELIA', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(24, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'nubia@teste.com', 'NUBIA', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(25, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'tiago@teste.com', 'TIAGO', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);  

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(26, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'joaorodrigues@teste.com', 'JOAO RODRIGUES', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);  

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(27, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'rosana@teste.com', 'ROSANNA', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);  

INSERT INTO public.usuarios("id","ativo","data_atualizacao","data_cadastro","data_inativacao","disponibilidade","email","nome","observacao","perfil","senha","telefone","ultimo_login","empresa_id")
VALUES(28, TRUE, NULL, TIMESTAMP '2026-06-11 14:09:04', NULL, TRUE, 'sandrinho@teste.com', 'SANDRINHO', NULL, 'MUSICO', '$2a$10$T9KlNwYYNwZLcm8Rpbt8KOXhoPMIi2e5tjZS9PkXrIAWlyQrO1KEe', '62994609450', NULL,2);  

--AGENDA-MENSAL
INSERT INTO public.agenda_mensal(
	ano, ativa, mes, departamento_id, empresa_id, id, descricao, status)
	VALUES (2026, TRUE, 10, 1, 2, 2, 'OUT-26', 'EM_MONTAGEM');	