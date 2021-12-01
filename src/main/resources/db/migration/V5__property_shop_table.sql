create table property_shop
(
    id      SERIAL PRIMARY KEY,
    shop_id INTEGER,
    name    CHARACTER VARYING(255),
    created TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (id)
);

insert into property_shop (name, shop_id) values ('FOOD', 1);
insert into property_shop (name, shop_id) values ('CLOTHES', 2);
insert into property_shop (name, shop_id) values ('BUILDING', 3);
insert into property_shop (name, shop_id) values ('FOOD', 4);
insert into property_shop (name, shop_id) values ('CLOTHES', 5);
insert into property_shop (name, shop_id) values ('BUILDING', 6);
insert into property_shop (name, shop_id) values ('FOOD', 7);
insert into property_shop (name, shop_id) values ('CLOTHES', 1);
insert into property_shop (name, shop_id) values ('CLOTHES', 3);
insert into property_shop (name, shop_id) values ('BUILDING', 1);
insert into property_shop (name, shop_id) values ('BUILDING', 2);