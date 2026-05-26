-- Clientes
INSERT INTO customers (name, email, phone, active) VALUES ('João Silva', 'joao.silva.dev@email.com', '21990000001', true);
INSERT INTO customers (name, email, phone, active) VALUES ('Maria Oliveira', 'maria.oliveira.dev@email.com', '21990000002', true);
INSERT INTO customers (name, email, phone, active) VALUES ('Carlos Souza', 'carlos.souza.dev@email.com', '21990000003', false);
INSERT INTO customers (name, email, phone, active) VALUES ('Ana Beatriz', 'ana.beatriz@email.com', '11988887777', true);
INSERT INTO customers (name, email, phone, active) VALUES ('Marcos Pereira', 'marcos.pereira@email.com', '11977776666', true);

-- Usuários (para os profissionais)
INSERT INTO app_users (username, password, enabled) VALUES ('camila.rocha@email.com', '123456', true);
INSERT INTO app_users (username, password, enabled) VALUES ('rodrigo.melo@email.com', '123456', true);
INSERT INTO app_users (username, password, enabled) VALUES ('patricia.lima@email.com', '123456', true);
INSERT INTO app_users (username, password, enabled) VALUES ('bruno.neves@email.com', '123456', true);

-- Profissionais
INSERT INTO professionals (name, specialty, active, app_user_id) VALUES ('Dra. Camila Rocha', 'Cardiologia', true, 1);
INSERT INTO professionals (name, specialty, active, app_user_id) VALUES ('Dr. Rodrigo Melo', 'Dermatologia', true, 2);
INSERT INTO professionals (name, specialty, active, app_user_id) VALUES ('Dra. Patrícia Lima', 'Nutrição', true, 3);
INSERT INTO professionals (name, specialty, active, app_user_id) VALUES ('Dr. Bruno Neves', 'Fisioterapia', false, 4);

-- Ofertas de Serviços
INSERT INTO service_offerings (name, duration_in_minutes, base_price, active) VALUES ('Consulta Cardiologia', 45, 250.00, true);
INSERT INTO service_offerings (name, duration_in_minutes, base_price, active) VALUES ('Consulta Dermatologia', 30, 200.00, true);
INSERT INTO service_offerings (name, duration_in_minutes, base_price, active) VALUES ('Consulta Nutrição', 60, 150.00, true);
INSERT INTO service_offerings (name, duration_in_minutes, base_price, active) VALUES ('Sessão Fisioterapia', 50, 180.00, true);
INSERT INTO service_offerings (name, duration_in_minutes, base_price, active) VALUES ('Check-up Completo', 90, 450.00, false);

-- Agendamentos de Exemplo
-- SCHEDULED
INSERT INTO appointments (customer_id, professional_id, service_offering_id, scheduled_at, status, notes, cancel_reason) 
VALUES (1, 1, 1, '2026-06-01T10:00:00', 'SCHEDULED', 'Paciente queixa-se de cansaço leve.', NULL);

-- CONFIRMED
INSERT INTO appointments (customer_id, professional_id, service_offering_id, scheduled_at, status, notes, cancel_reason) 
VALUES (2, 2, 2, '2026-06-01T14:30:00', 'CONFIRMED', 'Retorno para avaliação de tratamento de pele.', NULL);

-- DONE
INSERT INTO appointments (customer_id, professional_id, service_offering_id, scheduled_at, status, notes, cancel_reason) 
VALUES (4, 3, 3, '2026-05-20T09:00:00', 'DONE', 'Consulta inicial concluída com sucesso. Dieta recomendada.', NULL);

-- CANCELED
INSERT INTO appointments (customer_id, professional_id, service_offering_id, scheduled_at, status, notes, cancel_reason) 
VALUES (1, 2, 2, '2026-05-18T11:00:00', 'CANCELED', 'Desistiu devido a imprevisto no trabalho.', 'Conflito de horário do paciente');

-- NO_SHOW
INSERT INTO appointments (customer_id, professional_id, service_offering_id, scheduled_at, status, notes, cancel_reason) 
VALUES (5, 1, 1, '2026-05-19T16:00:00', 'NO_SHOW', 'Paciente não compareceu e não atendeu ligações.', NULL);
