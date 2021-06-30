create table shops
(
    id          SERIAL PRIMARY KEY,
    user_id     INTEGER,
    title       CHARACTER VARYING(200),
    description CHARACTER VARYING(300),
    address     CHARACTER VARYING(300),
    lat         INTEGER,
    lng         INTEGER,
    img         CHARACTER VARYING(200),
    url         CHARACTER VARYING(200),
    status      CHARACTER VARYING(100)   DEFAULT 'ACTIVE',
    created     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (id)
);
/*test shop*/
insert into shops (user_id, title, description, address, lat, lng, img, url) values ('1', 'MAGAS','OPISANIE','Moskow','20','30','0.png','2');