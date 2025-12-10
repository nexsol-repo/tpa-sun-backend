-- 1. 유저 (app_user)
CREATE TABLE app_user (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          company_code VARCHAR(255),
                          company_name VARCHAR(255),
                          name VARCHAR(255),
                          phone_number VARCHAR(255),
                          applicant_name VARCHAR(255),
                          applicant_email VARCHAR(255),
                          applicant_phone_number VARCHAR(255),
                          status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                          created_at DATETIME(6) NOT NULL,
                          updated_at DATETIME(6) NOT NULL
);

-- 2. 청약서 메인 (insurance_application)
CREATE TABLE insurance_application (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       application_number VARCHAR(255) NOT NULL UNIQUE,
                                       user_id BIGINT NOT NULL,
                                       insurance_status VARCHAR(20) NOT NULL,
    -- 신청자 정보 (Embedded)
                                       company_code VARCHAR(255),
                                       company_name VARCHAR(255),
                                       ceo_name VARCHAR(255),
                                       ceo_phone_number VARCHAR(255),
                                       applicant_name VARCHAR(255),
                                       applicant_phone_number VARCHAR(255),
                                       applicant_email VARCHAR(255),
    -- 약관 동의 (Embedded)
                                       re100_interest BOOLEAN,
                                       personal_info_collection_agreed BOOLEAN,
                                       personal_info_third_party_agreed BOOLEAN,
                                       group_rule_agreed BOOLEAN,
                                       marketing_agreed BOOLEAN,
                                       agreed_at DATETIME(6),
    -- 견적 정보 (Embedded)
                                       md_premium BIGINT,
                                       bi_premium BIGINT,
                                       gl_premium BIGINT,
                                       total_premium BIGINT,

                                       status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                                       created_at DATETIME(6) NOT NULL,
                                       updated_at DATETIME(6) NOT NULL
);

-- 3. 발전소 정보 (insurance_plant)
CREATE TABLE insurance_plant (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 application_id BIGINT NOT NULL,
                                 plant_name VARCHAR(255),
                                 address VARCHAR(255),
                                 region VARCHAR(255),
                                 capacity DECIMAL(19, 2),
                                 area DECIMAL(19, 2),
                                 inspection_date DATE,
                                 facility_type VARCHAR(255),
                                 drive_method VARCHAR(255),
                                 sales_target VARCHAR(255),

                                 status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                                 created_at DATETIME(6) NOT NULL,
                                 updated_at DATETIME(6) NOT NULL
);

-- 4. 가입 조건 (insurance_condition)
CREATE TABLE insurance_condition (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     application_id BIGINT NOT NULL,
                                     ess_installed BOOLEAN,
                                     property_damage_amount BIGINT,
                                     civil_work_included BOOLEAN,
                                     liability_amount BIGINT,
                                     business_interruption_amount BIGINT,
                                     start_date DATE,
    -- 질권 정보 (Embedded)
                                     pledge_bank_name VARCHAR(255),
                                     pledge_manager_name VARCHAR(255),
                                     pledge_manager_phone VARCHAR(255),
                                     pledge_amount BIGINT,
                                     pledge_address VARCHAR(255),
                                     pledge_bond_status VARCHAR(50), -- ENUM String
                                     pledge_remark TEXT,

                                     status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                                     created_at DATETIME(6) NOT NULL,
                                     updated_at DATETIME(6) NOT NULL
);

-- 5. 사고 이력 (accident_history) - 분리됨
CREATE TABLE accident_history (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  application_id BIGINT NOT NULL,
                                  accident_date DATE,
                                  accident_payment BIGINT,
                                  accident_content TEXT,

                                  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                                  created_at DATETIME(6) NOT NULL,
                                  updated_at DATETIME(6) NOT NULL
);

-- 6. 첨부 파일 (insurance_attachment)
CREATE TABLE insurance_attachment (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      application_id BIGINT NOT NULL,
                                      type VARCHAR(50) NOT NULL,
                                      file_key VARCHAR(255),
                                      original_file_name VARCHAR(255),
                                      extension VARCHAR(50),
                                      size BIGINT,

                                      status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                                      created_at DATETIME(6) NOT NULL,
                                      updated_at DATETIME(6) NOT NULL
);

-- 7. 기타 (이메일 인증, 리프레시 토큰, 요율 등 기존 테이블들)
CREATE TABLE email_verification (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    email VARCHAR(255),
                                    code VARCHAR(255),
                                    is_verified BOOLEAN,
                                    attempt_count INT,
                                    verified_type VARCHAR(50),
                                    sent_at DATETIME(6),
                                    expired_at DATETIME(6),
                                    verified_at DATETIME(6),
                                    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                                    created_at DATETIME(6) NOT NULL,
                                    updated_at DATETIME(6) NOT NULL
);

CREATE TABLE refresh_token (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               token VARCHAR(255),
                               user_id BIGINT,
                               issued_at DATETIME(6),
                               expired_at DATETIME(6),
                               status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                               created_at DATETIME(6) NOT NULL,
                               updated_at DATETIME(6) NOT NULL
);

CREATE TABLE insurance_rate (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                rate_type VARCHAR(50) NOT NULL,
                                rate_key VARCHAR(255) NOT NULL,
                                rate_value DECIMAL(19, 5) NOT NULL,
                                effective_date DATE,
                                status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                                created_at DATETIME(6) NOT NULL,
                                updated_at DATETIME(6) NOT NULL
);