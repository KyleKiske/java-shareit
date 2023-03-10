DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS users;
DROP TYPE IF EXISTS BOOKING_STATUS;


CREATE TYPE IF NOT EXISTS BOOKING_STATUS AS ENUM ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED');

CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email   VARCHAR(512) UNIQUE NOT NULL,
    name    VARCHAR(255)        NOT NULL
);

CREATE TABLE IF NOT EXISTS requests
(
    request_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    description  VARCHAR(512) NOT NULL,
    requester_id BIGINT       NOT NULL,
    created      TIMESTAMP    NOT NULL,
    FOREIGN KEY (requester_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items
(
    item_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(512) NOT NULL,
    available   BOOLEAN      NOT NULL,
    owner_id    BIGINT       NOT NULL,
    request_id  BIGINT,
    FOREIGN KEY (request_id) REFERENCES requests (request_id) ON DELETE NO ACTION,
    FOREIGN KEY (owner_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings
(
    booking_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_time TIMESTAMP      NOT NULL,
    end_time   TIMESTAMP      NOT NULL,
    item_id    INTEGER        NOT NULL,
    booker_id  INTEGER        NOT NULL,
    status     BOOKING_STATUS NOT NULL,
    FOREIGN KEY (item_id) REFERENCES items (item_id) ON DELETE CASCADE,
    FOREIGN KEY (booker_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments
(
    comment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    text       VARCHAR(255) NOT NULL,
    item_id    BIGINT       NOT NULL,
    author_id  BIGINT       NOT NULL,
    created    TIMESTAMP    NOT NULL,
    FOREIGN KEY (item_id) REFERENCES items (item_id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users (user_id) ON DELETE CASCADE
);
