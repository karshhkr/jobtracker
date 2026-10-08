CREATE TABLE users (
    id            VARCHAR(36)  NOT NULL,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'USER',
    created_at    DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB;

CREATE TABLE job_applications (
    id              VARCHAR(36)  NOT NULL,
    user_id         VARCHAR(36)  NOT NULL,
    company         VARCHAR(255) NOT NULL,
    role_title      VARCHAR(255) NOT NULL,
    location        VARCHAR(255),
    job_url         VARCHAR(1000),
    status          VARCHAR(20)  NOT NULL DEFAULT 'APPLIED',
    applied_date    DATE,
    follow_up_date  DATE,
    notes           TEXT,
    created_at      DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_job_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_job_user_created (user_id, created_at),
    INDEX idx_job_user_status (user_id, status)
) ENGINE=InnoDB;

CREATE TABLE subscriptions (
    id         VARCHAR(36) NOT NULL,
    user_id    VARCHAR(36) NOT NULL,
    plan       VARCHAR(10) NOT NULL DEFAULT 'FREE',
    expires_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_sub_user UNIQUE (user_id),
    CONSTRAINT fk_sub_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE payments (
    id                  VARCHAR(36)  NOT NULL,
    user_id             VARCHAR(36)  NOT NULL,
    razorpay_order_id   VARCHAR(100) NOT NULL,
    razorpay_payment_id VARCHAR(100),
    amount              BIGINT       NOT NULL,
    status              VARCHAR(10)  NOT NULL DEFAULT 'CREATED',
    created_at          DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_pay_order UNIQUE (razorpay_order_id),
    CONSTRAINT fk_pay_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE reminders (
    id                 VARCHAR(36) NOT NULL,
    user_id            VARCHAR(36) NOT NULL,
    job_application_id VARCHAR(36) NOT NULL,
    remind_at          DATE        NOT NULL,
    message            VARCHAR(500),
    notified           BOOLEAN     NOT NULL DEFAULT FALSE,
    done               BOOLEAN     NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT fk_rem_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_rem_job FOREIGN KEY (job_application_id) REFERENCES job_applications (id) ON DELETE CASCADE,
    INDEX idx_rem_due (remind_at, notified, done),
    INDEX idx_rem_user (user_id, done, remind_at)
) ENGINE=InnoDB;