CREATE TABLE client
(
    id         serial not null,
    email      text   not null,
    first_name text,
    last_name  text,
    version    int4   not null default 0,
    PRIMARY KEY (id)
);