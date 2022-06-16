-- CREATE TABLE Users (
--     username VARCHAR NOT NULL PRIMARY KEY UNIQUE,
--     first_name VARCHAR NOT NULL,
--     last_name VARCHAR NOT NULL,
--     password VARCHAR NOT NULL,
--     role VARCHAR NOT NULL DEFAULT 'user'
-- );

--  CREATE TABLE sales (
--         sales_id serial PRIMARY KEY UNIQUE NOT NULL,
--         customer_id int NOT NULL,
--         product_id int NOT NULL,
--         quantity int NOT NULL,
--         sales_date date NOT NULL,
--         amount_paid money NOT NULL
--  );

--   CREATE TABLE products (
--         product_id serial PRIMARY KEY UNIQUE NOT NULL,
--         name varchar NOT NULL,
--         type varchar NOT NULL,
--         price money NOT NULL,
--         quantity int NOT NULL,
--         sales_date date NOT NULL,
--         amount_paid money NOT NULL
--  );
 
--    CREATE TABLE customers (
--         customer_id serial PRIMARY KEY UNIQUE NOT NULL,
--         first_name varchar NOT NULL,
--         last_name varchar NOT NULL,
--         phone_num varchar NOT NULL
--  );
 