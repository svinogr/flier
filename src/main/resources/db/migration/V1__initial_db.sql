create table usr (
    Id SERIAL PRIMARY KEY,
    name CHARACTER VARYING(30),
    password CHARACTER VARYING(255),
    mail CHARACTER VARYING(30),
    role CHARACTER VARYING(30),
    UNIQUE(Id)
)