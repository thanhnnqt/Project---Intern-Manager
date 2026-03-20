-- Flyway Migration - V1__Initial_Schema.sql
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `comments`;
DROP TABLE IF EXISTS `notifications`;
DROP TABLE IF EXISTS `tasks`;
DROP TABLE IF EXISTS `interns`;
DROP TABLE IF EXISTS `mentors`;
DROP TABLE IF EXISTS `accounts`;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `accounts` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(255) NOT NULL UNIQUE,
    `password` VARCHAR(255),
    `role` VARCHAR(20) NOT NULL,
    `google_id` VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `mentors` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `full_name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `phone` VARCHAR(20),
    `avatar` TEXT,
    `department` VARCHAR(255),
    `position` VARCHAR(255),
    `account_id` BIGINT UNIQUE,
    FOREIGN KEY (`account_id`) REFERENCES `accounts`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `interns` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `full_name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `phone` VARCHAR(20),
    `university` VARCHAR(255),
    `major` VARCHAR(255),
    `status` VARCHAR(50),
    `avatar` TEXT,
    `account_id` BIGINT UNIQUE,
    `mentor_id` BIGINT,
    FOREIGN KEY (`account_id`) REFERENCES `accounts`(`id`),
    FOREIGN KEY (`mentor_id`) REFERENCES `mentors`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `tasks` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(255) NOT NULL,
    `description` TEXT,
    `status` VARCHAR(50),
    `priority` VARCHAR(50),
    `deadline` DATETIME,
    `intern_id` BIGINT,
    `mentor_id` BIGINT,
    FOREIGN KEY (`intern_id`) REFERENCES `interns`(`id`),
    FOREIGN KEY (`mentor_id`) REFERENCES `mentors`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `notifications` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(255) NOT NULL,
    `content` TEXT,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `is_read` BIT(1) NOT NULL DEFAULT 0,
    `account_id` BIGINT,
    FOREIGN KEY (`account_id`) REFERENCES `accounts`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `comments` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `content` TEXT NOT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `task_id` BIGINT,
    `account_id` BIGINT,
    FOREIGN KEY (`task_id`) REFERENCES `tasks`(`id`),
    FOREIGN KEY (`account_id`) REFERENCES `accounts`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
