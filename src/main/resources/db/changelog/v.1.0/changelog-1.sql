--liquibase formatted sql
--changeset Chizhara:1
CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100),
    patronymic VARCHAR(100),
    email      VARCHAR(320) NOT NULL UNIQUE,
    password   VARCHAR(60)  NOT NULL
);

CREATE TABLE tasks
(
    id           UUID PRIMARY KEY,
    name         VARCHAR(100)                NOT NULL,
    description  VARCHAR(2000)               NOT NULL,
    status       VARCHAR(50)                 NOT NULL,
    priority     VARCHAR(50)                 NOT NULL,
    creator_id   UUID REFERENCES users (id)  NOT NULL,
    performer_id UUID REFERENCES users (id)  NULL,
    created_on   TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE comments
(
    id              UUID PRIMARY KEY,
    text            VARCHAR(2000)               NOT NULL,
    author_id       UUID REFERENCES users (id)  NOT NULL,
    task_id         UUID REFERENCES tasks (id)  NOT NULL,
    created_on      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_updated_on TIMESTAMP WITHOUT TIME ZONE NULL
)