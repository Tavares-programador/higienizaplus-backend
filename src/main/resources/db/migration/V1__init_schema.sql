-- ============================================================
-- Higieniza+ - Schema inicial
-- ============================================================

CREATE TABLE admins (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(60)  NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    criado_em       TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE orcamentos (
    id              BIGSERIAL PRIMARY KEY,
    nome            VARCHAR(150) NOT NULL,
    email           VARCHAR(150) NOT NULL,
    whatsapp        VARCHAR(30)  NOT NULL,
    servico         VARCHAR(100) NOT NULL,
    mensagem        TEXT,
    status          VARCHAR(20)  NOT NULL DEFAULT 'NOVO',
    criado_em       TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX idx_orcamentos_criado_em ON orcamentos (criado_em DESC);
CREATE INDEX idx_orcamentos_status ON orcamentos (status);

CREATE TABLE servicos_preco (
    id              BIGSERIAL PRIMARY KEY,
    categoria       VARCHAR(80)   NOT NULL,
    item            VARCHAR(120)  NOT NULL,
    preco_kz        NUMERIC(12,2) NOT NULL,
    ordem           INT           NOT NULL DEFAULT 0
);

-- ============================================================
-- Dados iniciais da tabela de precos (Lista de Orcamento)
-- ============================================================

INSERT INTO servicos_preco (categoria, item, preco_kz, ordem) VALUES
('Lavagem de Sofás', 'Sofá de 2 lugares', 9500.00, 1),
('Lavagem de Sofás', 'Sofá de 3 lugares', 11500.00, 2),
('Lavagem de Sofás', 'Sofá de 4 lugares', 14000.00, 3),
('Lavagem de Sofás', 'Sofá de 5 lugares', 17000.00, 4),

('Lavagem de Colchões', 'Colchão solteiro', 11000.00, 5),
('Lavagem de Colchões', 'Colchão casal', 13500.00, 6),
('Lavagem de Colchões', 'Colchão king', 14500.00, 7),

('Outros', 'Cadeiras', 2000.00, 8),
('Outros', 'Poltrona', 5000.00, 9),
('Outros', 'Tapete (por m²)', 1500.00, 10),
('Outros', 'Puff', 2000.00, 11),

('Desinfestação Casa/Empresa', 'Casa ou Empresa (T1)', 15000.00, 12),
('Desinfestação Casa/Empresa', 'Casa ou Empresa (T2)', 25000.00, 13),
('Desinfestação Casa/Empresa', 'Casa ou Empresa (T3)', 30000.00, 14),
('Desinfestação Casa/Empresa', 'Casa ou Empresa (T4)', 35000.00, 15);
