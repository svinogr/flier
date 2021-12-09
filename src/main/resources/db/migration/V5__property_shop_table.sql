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
    UNIQUE (id, shop_id, property_id)
);

insert into property_shop (name)
values ('FOOD');
insert into property_shop (name)
values ('CLOTHES');
insert into property_shop (name)
values ('BUILDING');

insert into properties_shops (shop_id, property_id) values (1,1);
insert into properties_shops (shop_id, property_id) values (1,2);
insert into properties_shops (shop_id, property_id) values (2,1);
insert into properties_shops (shop_id, property_id) values (3,1);
insert into properties_shops (shop_id, property_id) values (3,3);
insert into properties_shops (shop_id, property_id) values (4,1);
insert into properties_shops (shop_id, property_id) values (4,2);
insert into properties_shops (shop_id, property_id) values (5,1);
insert into properties_shops (shop_id, property_id) values (5,3);
insert into properties_shops (shop_id, property_id) values (6,1);
insert into properties_shops (shop_id, property_id) values (6,2);
insert into properties_shops (shop_id, property_id) values (6,3);
insert into properties_shops (shop_id, property_id) values (7,1);


