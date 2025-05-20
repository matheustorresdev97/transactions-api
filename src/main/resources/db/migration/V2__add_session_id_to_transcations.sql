-- Adiciona coluna session_id à tabela tb_transactions
ALTER TABLE tb_transactions ADD COLUMN session_id UUID;

-- Cria um índice para a coluna session_id para melhorar a performance de consultas
CREATE INDEX idx_transactions_session_id ON tb_transactions(session_id);