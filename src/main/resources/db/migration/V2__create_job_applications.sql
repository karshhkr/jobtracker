CREATE TABLE job_applications (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    company VARCHAR(255),
    role_title VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'APPLIED',
    applied_date DATE,
    follow_up_date DATE,
    CONSTRAINT fk_job_app_user FOREIGN KEY (user_id) REFERENCES users(id)
);