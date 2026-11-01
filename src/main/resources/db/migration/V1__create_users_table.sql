CREATE TABLE users (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       full_name VARCHAR(100) NOT NULL,
                       email VARCHAR(150) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       PRIMARY KEY (id),

                       CONSTRAINT uk_users_email UNIQUE (email)
);