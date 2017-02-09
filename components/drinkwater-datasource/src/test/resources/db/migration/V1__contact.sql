CREATE TABLE contact (
    id INT NOT NULL PRIMARY KEY,
    first_name VARCHAR NOT NULL,
    last_name VARCHAR NOT NULL
);

CREATE TABLE trace (
    id serial primary key,
    correlationId character varying(256) NOT NULL,
    instant timestamp without time zone NOT NULL,
    name character varying(256) NOT NULL,
    description character varying(512),
    application character varying(512),
    service character varying(512),
    operation text,
    body text,
    headers text
);