--liquibase formatted sql

--changeset egor:001-create-tables

CREATE TABLE "user" (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    gender VARCHAR(10) CHECK (gender IN ('MALE', 'FEMALE')),
    age INTEGER CHECK (age > 0),
    weight NUMERIC(5, 2) CHECK (weight > 0),
    height NUMERIC(5, 2) CHECK (height > 0),
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE workout (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    performed_at TIMESTAMPTZ NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT fk_user_workout FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE
);

CREATE TABLE exercise_type (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE exercise (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    workout_id BIGINT NOT NULL,
    exercise_type_id BIGINT NOT NULL,
    description VARCHAR(50),
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT fk_exercise_workout FOREIGN KEY (workout_id) REFERENCES workout (id) ON DELETE CASCADE,
    CONSTRAINT fk_exercise_type FOREIGN KEY (exercise_type_id) REFERENCES exercise_type(id) ON DELETE RESTRICT
);

CREATE TABLE progress (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    set_number INTEGER NOT NULL CHECK (set_number > 0),
    repetition INTEGER NOT NULL CHECK (repetition > 0),
    weight NUMERIC(6,2) CHECK (weight >= 0),
    exercise_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT fk_progress_exercise FOREIGN KEY (exercise_id) REFERENCES exercise (id) ON DELETE CASCADE,
    CONSTRAINT uq_progress_set UNIQUE (exercise_id, set_number)
);

--rollback DROP TABLE progress;
--rollback DROP TABLE exercise;
--rollback DROP TABLE exercise_type;
--rollback DROP TABLE workout;
--rollback DROP TABLE "user";

