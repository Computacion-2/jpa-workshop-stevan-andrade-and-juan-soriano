-- ========== SEGURIDAD ==========
CREATE TABLE app_role (
    id   NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR2(50) NOT NULL,
    CONSTRAINT uk_role_name UNIQUE (name)
);

CREATE TABLE permission (
    id          NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR2(80) NOT NULL,
    description VARCHAR2(255),
    CONSTRAINT uk_permission_name UNIQUE (name)
);

CREATE TABLE role_permission (
    id            NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    role_id       NUMBER NOT NULL,
    permission_id NUMBER NOT NULL,
    CONSTRAINT fk_rp_role       FOREIGN KEY (role_id)       REFERENCES app_role(id),
    CONSTRAINT fk_rp_permission FOREIGN KEY (permission_id) REFERENCES permission(id),
    CONSTRAINT uk_role_permission UNIQUE (role_id, permission_id)
);

CREATE TABLE app_user (
    id         NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    role_id    NUMBER NOT NULL,
    name       VARCHAR2(100) NOT NULL,
    email      VARCHAR2(120) NOT NULL,
    password   VARCHAR2(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_user_email UNIQUE (email),
    CONSTRAINT fk_user_role  FOREIGN KEY (role_id) REFERENCES app_role(id)
);

-- ========== DOMINIO FINANCIERO ==========
CREATE TABLE account (
    id         NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    NUMBER NOT NULL,
    name       VARCHAR2(100) NOT NULL,
    type       VARCHAR2(20) NOT NULL,
    balance    NUMBER(14,2) DEFAULT 0 NOT NULL,
    currency   VARCHAR2(3) DEFAULT 'COP' NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_account_user FOREIGN KEY (user_id) REFERENCES app_user(id),
    CONSTRAINT ck_account_type CHECK (type IN ('EFECTIVO','BANCO','TARJETA','AHORROS'))
);

CREATE TABLE category (
    id      NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id NUMBER NOT NULL,
    name    VARCHAR2(80) NOT NULL,
    type    VARCHAR2(10) NOT NULL,
    icon    VARCHAR2(50),
    CONSTRAINT fk_category_user FOREIGN KEY (user_id) REFERENCES app_user(id),
    CONSTRAINT ck_category_type CHECK (type IN ('INGRESO','GASTO')),
    CONSTRAINT uk_category_user_name UNIQUE (user_id, name)
);

CREATE TABLE fin_transaction (
    id               NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    account_id       NUMBER NOT NULL,
    category_id      NUMBER NOT NULL,
    amount           NUMBER(14,2) NOT NULL,
    type             VARCHAR2(10) NOT NULL,
    description      VARCHAR2(255),
    transaction_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_tx_account  FOREIGN KEY (account_id)  REFERENCES account(id),
    CONSTRAINT fk_tx_category FOREIGN KEY (category_id) REFERENCES category(id),
    CONSTRAINT ck_tx_amount   CHECK (amount > 0),
    CONSTRAINT ck_tx_type     CHECK (type IN ('INGRESO','GASTO'))
);

CREATE TABLE budget (
    id           NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id      NUMBER NOT NULL,
    category_id  NUMBER NOT NULL,
    amount_limit NUMBER(14,2) NOT NULL,
    start_date   DATE NOT NULL,
    end_date     DATE NOT NULL,
    CONSTRAINT fk_budget_user     FOREIGN KEY (user_id)     REFERENCES app_user(id),
    CONSTRAINT fk_budget_category FOREIGN KEY (category_id) REFERENCES category(id),
    CONSTRAINT ck_budget_amount   CHECK (amount_limit > 0),
    CONSTRAINT ck_budget_dates    CHECK (end_date >= start_date)
);

CREATE TABLE period_summary (
    id            NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id       NUMBER NOT NULL,
    period_year   NUMBER(4) NOT NULL,
    period_month  NUMBER(2) NOT NULL,
    total_income  NUMBER(14,2) DEFAULT 0 NOT NULL,
    total_expense NUMBER(14,2) DEFAULT 0 NOT NULL,
    net_savings   NUMBER(14,2) DEFAULT 0 NOT NULL,
    savings_rate  NUMBER(5,2)  DEFAULT 0 NOT NULL,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_ps_user  FOREIGN KEY (user_id) REFERENCES app_user(id),
    CONSTRAINT ck_ps_month CHECK (period_month BETWEEN 1 AND 12),
    CONSTRAINT uk_ps_user_period UNIQUE (user_id, period_year, period_month)
);

CREATE TABLE period_category_summary (
    id                NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    period_summary_id NUMBER NOT NULL,
    category_id       NUMBER NOT NULL,
    total_amount      NUMBER(14,2) NOT NULL,
    percentage        NUMBER(5,2)  NOT NULL,
    transaction_count NUMBER(6)    NOT NULL,
    CONSTRAINT fk_pcs_summary  FOREIGN KEY (period_summary_id) REFERENCES period_summary(id) ON DELETE CASCADE,
    CONSTRAINT fk_pcs_category FOREIGN KEY (category_id)       REFERENCES category(id),
    CONSTRAINT uk_pcs UNIQUE (period_summary_id, category_id)
);

-- Índices para las consultas de comparación
CREATE INDEX idx_tx_account_date ON fin_transaction(account_id, transaction_date);
CREATE INDEX idx_tx_category     ON fin_transaction(category_id);
CREATE INDEX idx_user_role       ON app_user(role_id);
