DROP TABLE insurance_plant;

ALTER TABLE insurance_application
    ADD COLUMN plant_name VARCHAR(255),
    ADD COLUMN plant_address VARCHAR(255),
    ADD COLUMN plant_region VARCHAR(255),
    ADD COLUMN plant_capacity DECIMAL(19, 2),
    ADD COLUMN plant_area DECIMAL(19, 2),
    ADD COLUMN plant_inspection_date DATE,
    ADD COLUMN plant_facility_type VARCHAR(255),
    ADD COLUMN plant_drive_method VARCHAR(255),
    ADD COLUMN plant_sales_target VARCHAR(255);