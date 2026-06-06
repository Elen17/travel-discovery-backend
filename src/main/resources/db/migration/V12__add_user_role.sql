-- Add USER_ROLE table and populate it with initial values
CREATE TABLE IF NOT EXISTS USER_ROLE
(
    ID   SERIAL PRIMARY KEY,
    NAME VARCHAR(30) NOT NULL UNIQUE
);

INSERT INTO USER_ROLE(id, name)
values (1, 'ADMIN'),
       (2, 'USER');

ALTER TABLE USERS
    ADD ROLE_ID INT,
    ADD CONSTRAINT FK_ROLE_ID FOREIGN KEY (ROLE_ID) REFERENCES USER_ROLE (ID);

UPDATE USERS
SET ROLE_ID = 2;

ALTER TABLE USERS
    ALTER COLUMN ROLE_ID SET NOT NULL;

INSERT INTO USERS(ID, EMAIL, FULL_NAME, PASSWORD_HASH, ROLE_ID)
VALUES (-1, 'admin@admin.com',
        'Admin User',
        '$2a$10$KgcAXX94TtL6P3uekYymJOPviDn4t4TV219u9SGI/QHfk4bBWEttK', 1);

-- Keep the SERIAL sequence ahead of the explicit ids we just inserted.
SELECT setval(pg_get_serial_sequence('users', 'id'),
              (SELECT MAX(ID) FROM users));