-- Тестовые пользователи
INSERT INTO users (name, email) VALUES
('User One', 'user1@example.com'),
('User Two', 'user2@example.com'),
('User Three', 'user3@example.com');

-- Тестовые запросы
INSERT INTO requests (description, requestor_id, created) VALUES
('Нужна дрель', 1, CURRENT_TIMESTAMP),
('Ищу паяльник', 2, CURRENT_TIMESTAMP);

-- Тестовые items
INSERT INTO items (name, description, is_available, owner_id, request_id) VALUES
('Дрель', 'Мощная дрель', true, 1, NULL),
('Молоток', 'Прочный молоток', true, 2, NULL),
('Паяльник', 'Электрический паяльник', true, 3, 2);

-- Тестовые бронирования
INSERT INTO bookings (start_date, end_date, item_id, booker_id, status) VALUES
(CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1' DAY, 1, 2, 'APPROVED'),
(CURRENT_TIMESTAMP + INTERVAL '2' DAY, CURRENT_TIMESTAMP + INTERVAL '3' DAY, 2, 1, 'WAITING');

-- Тестовые комментарии
INSERT INTO comments (text, item_id, author_id, created) VALUES
('Отличная дрель!', 1, 2, CURRENT_TIMESTAMP),
('Хороший молоток', 2, 1, CURRENT_TIMESTAMP);