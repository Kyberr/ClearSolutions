CREATE TABLE users (
	id varchar(36),
	email VARCHAR(256) NOT NULL UNIQUE,
	first_name VARCHAR(256) NOT NULL,
	last_name VARCHAR(256) NOT NULL,
	birthdate DATE NOT NULL,
	address VARCHAR(256),
	phone_number VARCHAR(256),
	PRIMARY KEY (id)
);