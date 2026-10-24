CREATE TABLE users
(
    phone_number VARCHAR(15) PRIMARY KEY UNIQUE,
    email        VARCHAR(255),
    full_name    VARCHAR(255),
    avatar       VARCHAR(255),
    birth_date   DATE,
    created_at   TIMESTAMP,
    created_by   VARCHAR(255),
    updated_at   TIMESTAMP,
    updated_by   VARCHAR(255),
    locked       BOOL DEFAULT false,
    deleted      BOOL DEFAULT false
);

CREATE TABLE relationships
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    soft_id        VARCHAR(255) NOT NULL,
    phone_a        VARCHAR(255) NOT NULL,
    display_name_a VARCHAR(255),
    phone_b        VARCHAR(255) NOT NULL,
    display_name_b VARCHAR(255),
    status         VARCHAR(50)  NOT NULL DEFAULT "SELF",
    created_at     TIMESTAMP,
    created_by     VARCHAR(255),
    updated_at     TIMESTAMP,
    updated_by     VARCHAR(255),
    locked         BOOL                  DEFAULT false,
    deleted        BOOL                  DEFAULT false,
    CONSTRAINT friend_ships_users_a_phone_fk FOREIGN KEY (phone_a) REFERENCES users (phone_number),
    CONSTRAINT friend_ships_users_b_phone_fk FOREIGN KEY (phone_b) REFERENCES users (phone_number)
);

CREATE TABLE invite_add_friends
(
    id                    INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    phone_number_sender   VARCHAR(255) NOT NULL,
    phone_number_receiver VARCHAR(255) NOT NULL,
    accepted              BOOL default false,
    message               VARCHAR(255) NULL DEFAULT 'Xin chào bạn! Chúng mình kết bạn làm quen nhé!',
    created_at            TIMESTAMP,
    created_by            VARCHAR(255),
    updated_at            TIMESTAMP,
    updated_by            VARCHAR(255),
    locked                BOOL DEFAULT false,
    deleted               BOOL DEFAULT false,
    CONSTRAINT friend_ships_users_phone_number_sender_fk FOREIGN KEY (phone_number_sender) REFERENCES users (phone_number),
    CONSTRAINT friend_ships_users_phone_number_receiver_fk FOREIGN KEY (phone_number_receiver) REFERENCES users (phone_number)
);
