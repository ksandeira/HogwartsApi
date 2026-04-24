CREATE TABLE car (
    id SERIAL PRIMARY KEY,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2)
);

CREATE TABLE person (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT,
    has_driver_license BOOLEAN DEFAULT FALSE,
    car_id INT,
    FOREIGN KEY (car_id) REFERENCES car(id)
);