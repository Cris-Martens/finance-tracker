INSERT INTO app_user(username, email, password, authorities)
    VALUES ('Mark', 'mark@google.com', '$2a$10$xyh/VKW5IAR88h.j6f4AYe7NqnHEK6a47X8j8LyHrebGHKb20yxMG', 'ROLE_USER');
INSERT INTO app_user(username, email, password, authorities)
    VALUES ('JohnDoe', 'johndoe@gmail.com', '$2a$10$wfObPax/4e4eyTtHlzRVq.gYCJA/ZFV0Gj7Zz29soqm63KvbM7IAq', 'ROLE_USER');

INSERT INTO account_info(first_name, last_name, country, monthly_income, app_user)
    VALUES ('Mark', 'Marquez', 'Belgium', 2400, 2);
INSERT INTO account_info(first_name, last_name, country, monthly_income, app_user)
VALUES ('John', 'Doe', 'France', 2500, 3);