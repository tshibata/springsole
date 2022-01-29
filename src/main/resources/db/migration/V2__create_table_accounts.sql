CREATE TABLE accounts (
	id BIGSERIAL,
	username VARCHAR(15) UNIQUE NOT NULL,
	password VARCHAR(60) NOT NULL,
	description VARCHAR(1000) NOT NULL,
	valid BOOLEAN NOT NULL,
	PRIMARY KEY (id)
);
CREATE INDEX accounts_by_username ON accounts (username);
CREATE INDEX accounts_by_valid_and_id ON accounts (valid, id);

