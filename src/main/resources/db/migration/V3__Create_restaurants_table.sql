CREATE TABLE restaurants (
                             id BIGINT NOT NULL AUTO_INCREMENT,
                             owner_id BIGINT NOT NULL,
                             name VARCHAR(100) NOT NULL,
                             description VARCHAR(255),
                             address VARCHAR(255) NOT NULL,
                             phone VARCHAR(15),
                             approval_status VARCHAR(20) NOT NULL,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             CONSTRAINT pk_restaurants PRIMARY KEY (id),

                             CONSTRAINT fk_restaurants_owner
                                 FOREIGN KEY (owner_id)
                                     REFERENCES users(id)
                                     ON DELETE CASCADE
);