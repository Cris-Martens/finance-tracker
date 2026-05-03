CREATE TABLE app_user
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    username        VARCHAR(255) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    password        VARCHAR(255) NOT NULL,
    authority       VARCHAR(255) NULL,
    CONSTRAINT pk_app_user PRIMARY KEY (id)
);

CREATE TABLE category
(
    id   BIGINT       NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (id)
);

CREATE TABLE expenses (
                          id              BIGINT AUTO_INCREMENT NOT NULL,
                          expense_date    DATE   NOT NULL,
                          amount          DOUBLE NOT NULL,
                          description     VARCHAR(255) NULL,
                          user_id         BIGINT NOT NULL,
                          category_id     BIGINT NOT NULL,
                          CONSTRAINT pk_expenses PRIMARY KEY (id),
                          CONSTRAINT FK_EXPENSES_ON_USER
                              FOREIGN KEY (user_id) REFERENCES app_user(id)
                                  ON DELETE CASCADE
);

CREATE TABLE account_info (
                              id              BIGINT AUTO_INCREMENT NOT NULL,
                              first_name      VARCHAR(255),
                              last_name       VARCHAR(255),
                              country         VARCHAR(255),
                              monthly_income  VARCHAR(255),
                              user_id         BIGINT NOT NULL UNIQUE ,
                              CONSTRAINT pk_account_info PRIMARY KEY (id),
                              CONSTRAINT FK_ACCOUNT_INFO_ON_USER
                                  FOREIGN KEY (user_id) REFERENCES app_user (id)
                                      ON DELETE CASCADE
);

CREATE TABLE budget
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    user_id       BIGINT NOT NULL,
    amount        DOUBLE NOT NULL UNIQUE,
    category_id   BIGINT NOT NULL UNIQUE,
    CONSTRAINT PK_BUDGET PRIMARY KEY (id),
    CONSTRAINT FK_BUDGET_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id),
    CONSTRAINT FK_BUDGET_ON_USER
        FOREIGN KEY (user_id) REFERENCES app_user (id)
            ON DELETE CASCADE
);