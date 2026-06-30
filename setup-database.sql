-- ============================================================
-- Execute este script UMA VEZ, conectado como o superusuario
-- do Postgres (geralmente "postgres"), antes de subir o backend.
--
-- Como rodar:
--   sudo -u postgres psql -f setup-database.sql
-- ============================================================

CREATE DATABASE higienizaplus;

CREATE USER higienizaplus_user WITH ENCRYPTED PASSWORD 'troque_esta_senha';

GRANT ALL PRIVILEGES ON DATABASE higienizaplus TO higienizaplus_user;

-- No Postgres 15+, tambem e necessario conceder privilegios no schema public:
\c higienizaplus
GRANT ALL ON SCHEMA public TO higienizaplus_user;
