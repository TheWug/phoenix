

--

DROP TABLE users CASCADE;
DROP TABLE authorities CASCADE;

CREATE TABLE users (
  username VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  shared_secret VARCHAR(255) NOT NULL,
  enabled BOOLEAN NOT NULL,
  two_factor_enabled BOOLEAN NOT NULL,
  PRIMARY KEY (username)
);


CREATE TABLE authorities (
  username VARCHAR(255) NOT NULL,
  authority VARCHAR(255) NOT NULL,
  PRIMARY KEY (username),
  CONSTRAINT fk_user_authorities FOREIGN KEY (username) REFERENCES users (username)
);



-- TwoFAUser "user", has ROLE_USER
INSERT INTO users (username, password, shared_secret, enabled, two_factor_enabled)
VALUES ('user', '123456', 'X', TRUE, FALSE);

INSERT INTO authorities (username, authority)
VALUES ('user', 'ROLE_USER');


-- TwoFAUser "admin" has ROLE_ADMIN
INSERT INTO users (username, password, shared_secret, enabled, two_factor_enabled)
VALUES ('admin', '123456', 'X', TRUE, FALSE);

INSERT INTO authorities (username, authority)
VALUES ('admin', 'ROLE_ADMIN');

