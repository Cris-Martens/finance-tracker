CREATE TABLE account_info
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    first_name      VARCHAR(255),
    last_name       VARCHAR(255),
    country         VARCHAR(255),
    user_id         BIGINT NOT NULL UNIQUE ,
    CONSTRAINT pk_account_info PRIMARY KEY (id)
);

ALTER TABLE account_info
    ADD CONSTRAINT FK_ACCOUNT_INFO_ON_USER FOREIGN KEY (user_id) REFERENCES app_user (id);