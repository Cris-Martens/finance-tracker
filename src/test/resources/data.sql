INSERT INTO app_user(username, email, password, authority)
    VALUES ('Mark', 'mark@google.com', 'ValidPass123!', 'ROLE_USER'),
           ('JohnDoe', 'johndoe@gmail.com', '$2a$10$wfObPax/4e4eyTtHlzRVq.gYCJA/ZFV0Gj7Zz29soqm63KvbM7IAq', 'ROLE_USER');

INSERT INTO account_info(first_name, last_name, country, monthly_income, user_id)
    VALUES ('Mark', 'Marquez', 'Belgium', 2400, 1),
           ('John', 'Doe', 'France', 2500, 2);

INSERT INTO category (id, name)
    VALUES  (1, 'Housing'),
            (2, 'Utilities'),
            (3, 'Groceries'),
            (4, 'Dining out/ takeout'),
            (5, 'Transportation'),
            (6, 'Insurance'),
            (7, 'Healthcare'),
            (8, 'Personal Care'),
            (9, 'Clothing & Shoes'),
            (10, 'Child Care'),
            (11, 'Pets'),
            (12, 'Subscriptions'),
            (13, 'Entertainment'),
            (14, 'Travel'),
            (15, 'Education');

INSERT INTO budget (user_id, amount, category_id)
    VALUES (1, 250.0, 2),
           (1, 850.0, 1),
           (1, 50.0, 5);

INSERT INTO expenses (expense_date, amount, description, user_id, category_id)
    VALUES ('2026-04-28', 690.00, 'Rent', 1, 1),
           ('2026-04-19', 10.0, 'Metro', 1, 5),
           ('2026-02-17', 30.0, 'Netflix', 1, 12),
           ('2026-03-12', 70.0, 'Dentist', 1, 7),
           ('2026-04-15', 100.0, 'Food', 1, 3),
           ('2026-03-24', 180.00, '', 1, 2);
