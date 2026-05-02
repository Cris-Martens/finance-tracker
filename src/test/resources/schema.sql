DROP TABLE IF EXISTS account_info;
DROP TABLE IF EXISTS app_user;

CREATE TABLE account_info (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name      VARCHAR(255),
    last_name       VARCHAR(255),
    country         VARCHAR(255),
    monthly_income  VARCHAR(255),
    user_id         BIGINT NOT NULL UNIQUE ,
    CONSTRAINT pk_account_info PRIMARY KEY (id)
);

CREATE TABLE app_user
(
    id        BIGINT AUTO_INCREMENT NOT NULL,
    username  VARCHAR(255) NOT NULL,
    email     VARCHAR(255) NOT NULL,
    password  VARCHAR(255) NOT NULL,
    authority VARCHAR(255) NULL,
    CONSTRAINT pk_app_user PRIMARY KEY (id)
);