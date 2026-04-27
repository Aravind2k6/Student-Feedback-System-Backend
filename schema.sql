-- ============================================================
--  Student Feedback System — MySQL Schema
--  Run this in MySQL Command Line Client:
--    source d:/Fsad-Frontend/student-feedback-backend/schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS Student_Feedback_System
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE Student_Feedback_System;

-- ─── Users ───────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(120)                    NOT NULL,
    email       VARCHAR(200)                    NOT NULL UNIQUE,
    username    VARCHAR(100)                    NOT NULL UNIQUE,
    password    VARCHAR(255)                    NOT NULL,
    role        ENUM('student', 'admin')        NOT NULL DEFAULT 'student'
);

SET @drop_reset_token = (
    SELECT IF(
        COUNT(*) > 0,
        'ALTER TABLE users DROP COLUMN reset_token',
        'SELECT 1'
    )
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'users'
      AND column_name = 'reset_token'
);
PREPARE stmt FROM @drop_reset_token;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @drop_reset_token_expiry = (
    SELECT IF(
        COUNT(*) > 0,
        'ALTER TABLE users DROP COLUMN reset_token_expiry',
        'SELECT 1'
    )
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'users'
      AND column_name = 'reset_token_expiry'
);
PREPARE stmt FROM @drop_reset_token_expiry;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    token       VARCHAR(120)                    NOT NULL UNIQUE,
    user_id     BIGINT                          NOT NULL,
    expires_at  DATETIME                        NOT NULL,
    created_at  DATETIME                        NOT NULL,
    CONSTRAINT fk_password_reset_tokens_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);

-- ─── Courses ──────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS courses (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)                     NOT NULL UNIQUE,
    code        VARCHAR(50)                     NOT NULL,
    course_name VARCHAR(200)                    NOT NULL,
    instructor  VARCHAR(120)                    NOT NULL,
    credits     INT,
    released    TINYINT(1)                      NOT NULL DEFAULT 1
);

-- ─── Feedback Forms ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS feedback_forms (
    id          VARCHAR(80)                     NOT NULL PRIMARY KEY,
    title       VARCHAR(300)                    NOT NULL,
    description TEXT,
    created_at  DATETIME                        NOT NULL,
    deadline    VARCHAR(20),
    published   TINYINT(1)                      NOT NULL DEFAULT 1,
    deadline_reminder_sent_at DATETIME,
    type        VARCHAR(50),
    target      VARCHAR(300),
    course      VARCHAR(50)
);

-- ─── Form Fields ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS form_fields (
    db_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    field_id    VARCHAR(20)                     NOT NULL,
    form_id     VARCHAR(80)                     NOT NULL,
    label       TEXT                            NOT NULL,
    field_type  VARCHAR(50)                     NOT NULL,
    required    TINYINT(1)                      NOT NULL DEFAULT 1,
    options     TEXT,
    sort_order  INT                             NOT NULL DEFAULT 0,
    CONSTRAINT fk_form_fields_form
        FOREIGN KEY (form_id) REFERENCES feedback_forms(id)
        ON DELETE CASCADE
);

-- ─── Feedback Submissions ─────────────────────────────────────
CREATE TABLE IF NOT EXISTS feedback_submissions (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    submission_key  VARCHAR(300)                NOT NULL UNIQUE,
    form_id         VARCHAR(80)                 NOT NULL,
    student_id      BIGINT                      NOT NULL,
    course          VARCHAR(50)                 NOT NULL,
    instructor      VARCHAR(120)                NOT NULL,
    overall_rating  INT,
    dynamic_ratings TEXT,
    remarks         TEXT,
    submitted_at    DATETIME                    NOT NULL,
    CONSTRAINT fk_submissions_student
        FOREIGN KEY (student_id) REFERENCES users(id)
        ON DELETE CASCADE
);

-- ─── Notifications ────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS notifications (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    type        VARCHAR(50)                     NOT NULL,
    message     TEXT                            NOT NULL,
    metadata    TEXT,
    timestamp   DATETIME                        NOT NULL,
    is_read     TINYINT(1)                      NOT NULL DEFAULT 0,
    user_id     BIGINT,      -- notifications are stored per user
    CONSTRAINT fk_notifications_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE OR REPLACE VIEW users_display AS
SELECT
    id,
    name,
    email,
    username,
    role
FROM users;

CREATE OR REPLACE VIEW form_fields_display AS
SELECT
    form_id,
    field_id,
    label AS question,
    field_type AS type,
    required AS is_required,
    options AS choices,
    sort_order AS order_no
FROM form_fields
ORDER BY form_id, sort_order;
