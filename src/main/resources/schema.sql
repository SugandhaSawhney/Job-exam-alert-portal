CREATE TABLE IF NOT EXISTS job_alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    organization VARCHAR(255),
    type VARCHAR(50),
    last_date VARCHAR(50),
    description VARCHAR(500),
    link VARCHAR(500)
);