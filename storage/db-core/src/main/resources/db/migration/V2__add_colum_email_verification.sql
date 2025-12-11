TRUNCATE TABLE email_verification;

ALTER TABLE email_verification ADD COLUMN company_code VARCHAR(255) NOT NULL;