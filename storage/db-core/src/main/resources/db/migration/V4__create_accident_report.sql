-- 1. 사고 접수 메인 테이블
CREATE TABLE accident_report (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 accident_number VARCHAR(255) NOT NULL UNIQUE,
                                 user_id BIGINT NOT NULL,
                                 application_id BIGINT NOT NULL,
                                 accident_status VARCHAR(50) NOT NULL,
                                 reported_at DATETIME(6),
                                 accident_type VARCHAR(50),
                                 accident_date DATETIME(6),
                                 accident_place VARCHAR(255),
                                 damage_description TEXT,
                                 estimated_loss_amount BIGINT,
                                 claimant_name VARCHAR(255),
                                 claimant_contact VARCHAR(255),
                                 account_bank VARCHAR(100),
                                 account_number VARCHAR(100),
                                 account_holder VARCHAR(100),
                                 status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                                 created_at DATETIME(6) NOT NULL,
                                 updated_at DATETIME(6) NOT NULL
);

-- 2. 사고 접수 첨부파일 테이블
CREATE TABLE accident_attachment (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                     accident_report_id BIGINT NOT NULL,
                                     attachment_type VARCHAR(100),
                                     file_key VARCHAR(255),
                                     original_file_name VARCHAR(255),
                                     extension VARCHAR(50),
                                     size BIGINT,
                                     status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                                     created_at DATETIME(6) NOT NULL,
                                     updated_at DATETIME(6) NOT NULL
);


CREATE INDEX idx_accident_report_user_id ON accident_report (user_id);
CREATE INDEX idx_accident_report_application_id ON accident_report (application_id);
CREATE INDEX idx_accident_attachment_report_id ON accident_attachment (accident_report_id);