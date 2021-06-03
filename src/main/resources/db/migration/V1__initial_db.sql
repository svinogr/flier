create table usr (
    Id SERIAL PRIMARY KEY,
    user_name CHARACTER VARYING(255),
     email CHARACTER VARYING(255),
    password CHARACTER VARYING(255),
    status CHARACTER VARYING(100) DEFAULT 'ACTIVE',
    created TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP WITH TIME ZONE  DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(Id)
);
create table role (
    Id SERIAL PRIMARY KEY,
    name CHARACTER VARYING(255),
    status CHARACTER VARYING(100) DEFAULT 'ACTIVE',
    created TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP WITH TIME ZONE  DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(Id)
);
create table user_roles (
Id SERIAL PRIMARY key,
 user_id INTEGER,
 role_id INTEGER
);