-- https://stackoverflow.com/questions/55984115/mysql-server-saves-localdatetime-with-3-hours-beyond-os-time
-- case sensitive name by default for unix image
-- configured insensitive mysql-compose.yml
USE recall;
DROP TABLE IF EXISTS product;
CREATE TABLE product(
    pdt_uniq_id BINARY(16) PRIMARY KEY DEFAULT (uuid_to_bin(uuid()))
    ,event_time TIMESTAMP -- WITHOUT TIME ZONE
    ,event_type NVARCHAR(128)
    ,product_id LONG
    ,category_id LONG
    ,category_code NVARCHAR(128)
    ,brand NVARCHAR(128)
    ,price FLOAT
    ,user_id LONG
    ,user_session NVARCHAR(128)
);