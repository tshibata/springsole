CREATE TABLE accounts (
	id BIGSERIAL,
	username VARCHAR(15) NOT NULL,
	password VARCHAR(60) NOT NULL,
	description VARCHAR(1000) NOT NULL,
	valid BOOLEAN NOT NULL,
	CONSTRAINT account_username_uniq UNIQUE (username),
	PRIMARY KEY (id)
);
CREATE INDEX accounts_by_username ON accounts (username);
CREATE INDEX accounts_by_valid_and_id ON accounts (valid, id);

