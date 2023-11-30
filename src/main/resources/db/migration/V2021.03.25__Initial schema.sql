CREATE TABLE account
(
    id      serial not null,
    balance int4   not null default 0,
    version int4   not null default 0,
    PRIMARY KEY (id)
);