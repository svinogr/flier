create table property_shop
(
    id      SERIAL PRIMARY KEY,
    name    CHARACTER VARYING(255),
    created TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (id)
);

insert into property_shop (name) values ('FOOD');
insert into property_shop (name) values ('CLOTHES');
insert into property_shop (name) values ('BUILDING');