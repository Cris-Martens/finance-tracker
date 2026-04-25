CREATE TABLE app_user
(
    id        BIGINT AUTO_INCREMENT NOT NULL,
    username  VARCHAR(255) NOT NULL,
    email     VARCHAR(255) NOT NULL,
    password  VARCHAR(255) NOT NULL,
    authority VARCHAR(255) NULL,
    CONSTRAINT pk_app_user PRIMARY KEY (id)
);

CREATE TABLE category
(
    id   BIGINT       NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (id)
);

CREATE TABLE expenses
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    expense_date  date   NOT NULL,
    amount DOUBLE NOT NULL,
    description VARCHAR(255) NULL,
    user_id       BIGINT NOT NULL,
    category_id   BIGINT NOT NULL,
    CONSTRAINT pk_expenses PRIMARY KEY (id)
);

ALTER TABLE app_user
    ADD CONSTRAINT uc_app_user_email UNIQUE (email);

ALTER TABLE app_user
    ADD CONSTRAINT uc_app_user_username UNIQUE (username);

ALTER TABLE category
    ADD CONSTRAINT uc_category_name UNIQUE (name);

ALTER TABLE expenses
    ADD CONSTRAINT FK_EXPENSES_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);

ALTER TABLE expenses
    ADD CONSTRAINT FK_EXPENSES_ON_USER FOREIGN KEY (user_id) REFERENCES app_user (id);