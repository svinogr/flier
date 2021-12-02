create table usr
(
    id        SERIAL PRIMARY KEY,
    user_name CHARACTER VARYING(255),
    email     CHARACTER VARYING(255),
    name      CHARACTER VARYING(255),
    surname   CHARACTER VARYING(255),
    phone     CHARACTER VARYING(255),
    password  CHARACTER VARYING(255),
    status    CHARACTER VARYING(100)   DEFAULT 'ACTIVE',
    created   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (id, email)
);
create table role
(
    id      SERIAL PRIMARY KEY,
    name    CHARACTER VARYING(255),
    status  CHARACTER VARYING(100)   DEFAULT 'ACTIVE',
    created TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (id)
);
create table user_roles
(
    id      SERIAL PRIMARY key,
    user_id INTEGER,
    role_id INTEGER,
     UNIQUE (id)
);