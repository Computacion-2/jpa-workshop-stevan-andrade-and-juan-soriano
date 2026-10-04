-- Datos iniciales. Las contraseñas con PENDING_HASH las cifra DatabaseInitializer con BCrypt
-- No usar punto y coma dentro de los comentarios

INSERT INTO permission (name, description) VALUES ('USER_READ', 'Consultar usuarios');
INSERT INTO permission (name, description) VALUES ('USER_WRITE', 'Crear y editar usuarios');
INSERT INTO permission (name, description) VALUES ('ROLE_READ', 'Consultar roles');
INSERT INTO permission (name, description) VALUES ('ROLE_WRITE', 'Crear y editar roles');
INSERT INTO permission (name, description) VALUES ('PERMISSION_READ', 'Consultar permisos');
INSERT INTO permission (name, description) VALUES ('PERMISSION_WRITE', 'Crear y editar permisos');
INSERT INTO permission (name, description) VALUES ('TRANSACTION_READ', 'Consultar movimientos');
INSERT INTO permission (name, description) VALUES ('TRANSACTION_WRITE', 'Registrar movimientos');

INSERT INTO app_role (name) VALUES ('ADMIN');
INSERT INTO app_role (name) VALUES ('USER');

INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM app_role r CROSS JOIN permission p WHERE r.name = 'ADMIN';

INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM app_role r, permission p
WHERE r.name = 'USER' AND p.name IN ('TRANSACTION_READ', 'TRANSACTION_WRITE');

INSERT INTO app_user (role_id, name, email, password)
SELECT id, 'Administrador', 'admin@finanzas.com', 'PENDING_HASH' FROM app_role WHERE name = 'ADMIN';
INSERT INTO app_user (role_id, name, email, password)
SELECT id, 'Ana Gómez', 'ana@finanzas.com', 'PENDING_HASH' FROM app_role WHERE name = 'USER';

INSERT INTO account (user_id, name, type, balance)
SELECT id, 'Bancolombia', 'BANCO', 2770000 FROM app_user WHERE email = 'ana@finanzas.com';
INSERT INTO account (user_id, name, type, balance)
SELECT id, 'Efectivo', 'EFECTIVO', 100000 FROM app_user WHERE email = 'ana@finanzas.com';

INSERT INTO category (user_id, name, type, icon) SELECT id, 'Salario', 'INGRESO', 'cash' FROM app_user WHERE email = 'ana@finanzas.com';
INSERT INTO category (user_id, name, type, icon) SELECT id, 'Freelance', 'INGRESO', 'laptop' FROM app_user WHERE email = 'ana@finanzas.com';
INSERT INTO category (user_id, name, type, icon) SELECT id, 'Arriendo', 'GASTO', 'home' FROM app_user WHERE email = 'ana@finanzas.com';
INSERT INTO category (user_id, name, type, icon) SELECT id, 'Comida', 'GASTO', 'food' FROM app_user WHERE email = 'ana@finanzas.com';
INSERT INTO category (user_id, name, type, icon) SELECT id, 'Transporte', 'GASTO', 'bus' FROM app_user WHERE email = 'ana@finanzas.com';
INSERT INTO category (user_id, name, type, icon) SELECT id, 'Entretenimiento', 'GASTO', 'movie' FROM app_user WHERE email = 'ana@finanzas.com';

-- Movimientos de agosto 2026
INSERT INTO fin_transaction (account_id, category_id, amount, type, description, transaction_date)
SELECT a.id, c.id, 2500000, 'INGRESO', 'Salario agosto', DATE '2026-08-30' FROM account a, category c WHERE a.name = 'Bancolombia' AND c.name = 'Salario';
INSERT INTO fin_transaction (account_id, category_id, amount, type, description, transaction_date)
SELECT a.id, c.id, 400000, 'INGRESO', 'Proyecto freelance', DATE '2026-08-15' FROM account a, category c WHERE a.name = 'Bancolombia' AND c.name = 'Freelance';
INSERT INTO fin_transaction (account_id, category_id, amount, type, description, transaction_date)
SELECT a.id, c.id, 800000, 'GASTO', 'Arriendo agosto', DATE '2026-08-02' FROM account a, category c WHERE a.name = 'Bancolombia' AND c.name = 'Arriendo';
INSERT INTO fin_transaction (account_id, category_id, amount, type, description, transaction_date)
SELECT a.id, c.id, 450000, 'GASTO', 'Mercado y restaurantes', DATE '2026-08-10' FROM account a, category c WHERE a.name = 'Bancolombia' AND c.name = 'Comida';
INSERT INTO fin_transaction (account_id, category_id, amount, type, description, transaction_date)
SELECT a.id, c.id, 120000, 'GASTO', 'Transporte público', DATE '2026-08-12' FROM account a, category c WHERE a.name = 'Efectivo' AND c.name = 'Transporte';
INSERT INTO fin_transaction (account_id, category_id, amount, type, description, transaction_date)
SELECT a.id, c.id, 150000, 'GASTO', 'Cine y salidas', DATE '2026-08-20' FROM account a, category c WHERE a.name = 'Efectivo' AND c.name = 'Entretenimiento';

-- Movimientos de septiembre 2026
INSERT INTO fin_transaction (account_id, category_id, amount, type, description, transaction_date)
SELECT a.id, c.id, 2500000, 'INGRESO', 'Salario septiembre', DATE '2026-09-30' FROM account a, category c WHERE a.name = 'Bancolombia' AND c.name = 'Salario';
INSERT INTO fin_transaction (account_id, category_id, amount, type, description, transaction_date)
SELECT a.id, c.id, 200000, 'INGRESO', 'Proyecto freelance', DATE '2026-09-18' FROM account a, category c WHERE a.name = 'Bancolombia' AND c.name = 'Freelance';
INSERT INTO fin_transaction (account_id, category_id, amount, type, description, transaction_date)
SELECT a.id, c.id, 800000, 'GASTO', 'Arriendo septiembre', DATE '2026-09-02' FROM account a, category c WHERE a.name = 'Bancolombia' AND c.name = 'Arriendo';
INSERT INTO fin_transaction (account_id, category_id, amount, type, description, transaction_date)
SELECT a.id, c.id, 320000, 'GASTO', 'Mercado del mes', DATE '2026-09-05' FROM account a, category c WHERE a.name = 'Bancolombia' AND c.name = 'Comida';
INSERT INTO fin_transaction (account_id, category_id, amount, type, description, transaction_date)
SELECT a.id, c.id, 100000, 'GASTO', 'Transporte público', DATE '2026-09-11' FROM account a, category c WHERE a.name = 'Efectivo' AND c.name = 'Transporte';
INSERT INTO fin_transaction (account_id, category_id, amount, type, description, transaction_date)
SELECT a.id, c.id, 90000, 'GASTO', 'Cine', DATE '2026-09-22' FROM account a, category c WHERE a.name = 'Efectivo' AND c.name = 'Entretenimiento';

-- Presupuestos de septiembre (plan vs realidad)
INSERT INTO budget (user_id, category_id, amount_limit, start_date, end_date)
SELECT u.id, c.id, 400000, DATE '2026-09-01', DATE '2026-09-30' FROM app_user u, category c WHERE u.email = 'ana@finanzas.com' AND c.name = 'Comida';
INSERT INTO budget (user_id, category_id, amount_limit, start_date, end_date)
SELECT u.id, c.id, 150000, DATE '2026-09-01', DATE '2026-09-30' FROM app_user u, category c WHERE u.email = 'ana@finanzas.com' AND c.name = 'Transporte';
INSERT INTO budget (user_id, category_id, amount_limit, start_date, end_date)
SELECT u.id, c.id, 150000, DATE '2026-09-01', DATE '2026-09-30' FROM app_user u, category c WHERE u.email = 'ana@finanzas.com' AND c.name = 'Entretenimiento';

-- Resúmenes mensuales (foto fija para comparar mes contra mes)
INSERT INTO period_summary (user_id, period_year, period_month, total_income, total_expense, net_savings, savings_rate)
SELECT id, 2026, 8, 2900000, 1520000, 1380000, 47.59 FROM app_user WHERE email = 'ana@finanzas.com';
INSERT INTO period_summary (user_id, period_year, period_month, total_income, total_expense, net_savings, savings_rate)
SELECT id, 2026, 9, 2700000, 1310000, 1390000, 51.48 FROM app_user WHERE email = 'ana@finanzas.com';

-- Desglose de gastos por categoría: agosto
INSERT INTO period_category_summary (period_summary_id, category_id, total_amount, percentage, transaction_count)
SELECT ps.id, c.id, 800000, 52.63, 1 FROM period_summary ps, category c WHERE ps.period_month = 8 AND c.name = 'Arriendo';
INSERT INTO period_category_summary (period_summary_id, category_id, total_amount, percentage, transaction_count)
SELECT ps.id, c.id, 450000, 29.61, 1 FROM period_summary ps, category c WHERE ps.period_month = 8 AND c.name = 'Comida';
INSERT INTO period_category_summary (period_summary_id, category_id, total_amount, percentage, transaction_count)
SELECT ps.id, c.id, 120000, 7.89, 1 FROM period_summary ps, category c WHERE ps.period_month = 8 AND c.name = 'Transporte';
INSERT INTO period_category_summary (period_summary_id, category_id, total_amount, percentage, transaction_count)
SELECT ps.id, c.id, 150000, 9.87, 1 FROM period_summary ps, category c WHERE ps.period_month = 8 AND c.name = 'Entretenimiento';

-- Desglose de gastos por categoría: septiembre
INSERT INTO period_category_summary (period_summary_id, category_id, total_amount, percentage, transaction_count)
SELECT ps.id, c.id, 800000, 61.07, 1 FROM period_summary ps, category c WHERE ps.period_month = 9 AND c.name = 'Arriendo';
INSERT INTO period_category_summary (period_summary_id, category_id, total_amount, percentage, transaction_count)
SELECT ps.id, c.id, 320000, 24.43, 1 FROM period_summary ps, category c WHERE ps.period_month = 9 AND c.name = 'Comida';
INSERT INTO period_category_summary (period_summary_id, category_id, total_amount, percentage, transaction_count)
SELECT ps.id, c.id, 100000, 7.63, 1 FROM period_summary ps, category c WHERE ps.period_month = 9 AND c.name = 'Transporte';
INSERT INTO period_category_summary (period_summary_id, category_id, total_amount, percentage, transaction_count)
SELECT ps.id, c.id, 90000, 6.87, 1 FROM period_summary ps, category c WHERE ps.period_month = 9 AND c.name = 'Entretenimiento';
