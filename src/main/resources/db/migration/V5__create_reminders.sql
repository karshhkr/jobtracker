CREATE TABLE reminders (
    id VARCHAR(36) PRIMARY KEY,
    application_id VARCHAR(36) NOT NULL,
    remind_at TIMESTAMP NOT NULL,
    sent BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_reminder_app FOREIGN KEY (application_id) REFERENCES job_applications(id)
);