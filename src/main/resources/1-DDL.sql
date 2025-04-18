\c kafkaDB;

CREATE TABLE IF NOT EXISTS accounts
(
    id uuid PRIMARY KEY,
    balance bigint
);

ALTER TABLE accounts OWNER TO db_user;

CREATE TABLE IF NOT EXISTS transfers
(
    id uuid PRIMARY KEY,
    amount bigint,
    status VARCHAR(20),
    sender_id uuid,
    receiver_id uuid,
    FOREIGN KEY(sender_id) REFERENCES accounts(id),
    FOREIGN KEY(receiver_id) REFERENCES accounts(id)
);

ALTER TABLE transfers OWNER TO db_user;