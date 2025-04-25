
CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT uk_user_email UNIQUE (email)
);

CREATE TABLE tasks (
    task_id BIGSERIAL PRIMARY KEY,
    task_text VARCHAR(255) NOT NULL,
    due_date TIMESTAMP,
    creation_date TIMESTAMP NOT NULL,
    is_complete BOOLEAN NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_task_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE notifications (
    notification_id BIGSERIAL PRIMARY KEY,
    text VARCHAR(255) NOT NULL,
    date TIMESTAMP NOT NULL,
    task_id BIGINT NOT NULL,
    CONSTRAINT fk_notification_task FOREIGN KEY (task_id) REFERENCES tasks(task_id) ON DELETE CASCADE
);

CREATE INDEX idx_task_user_id ON tasks(user_id);
CREATE INDEX idx_notification_task_id ON notifications(task_id);