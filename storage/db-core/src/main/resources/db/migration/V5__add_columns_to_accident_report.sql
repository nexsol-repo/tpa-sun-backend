ALTER TABLE accident_report
    DROP COLUMN claimant_name,
    DROP COLUMN claimant_contact;

ALTER TABLE accident_report
    ADD COLUMN plant_name VARCHAR(255),
    ADD COLUMN insured_name VARCHAR(255),
    ADD COLUMN insured_phone VARCHAR(255);