create table property_shop
(
    id      SERIAL PRIMARY KEY,
    name    CHARACTER VARYING(255),
    created TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status  CHARACTER VARYING(100)   DEFAULT 'ACTIVE',
    UNIQUE (id)
);

create table properties_shops
(
    id          SERIAL PRIMARY KEY,
    shop_id     INTEGER,
    property_id INTEGER,
    created     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status      CHARACTER VARYING(100)   DEFAULT 'ACTIVE',
    UNIQUE (id)
);

insert into property_shop (name)
values ('FOOD');
insert into property_shop (name)
values ('CLOTHES');
insert into property_shop (name)
values ('BUILDING');

