create table stocks (
   id SERIAL PRIMARY KEY,
   shop_id INTEGER,
   title CHARACTER VARYING(50),
   description CHARACTER VARYING(300),
   img CHARACTER VARYING(200),
   url CHARACTER VARYING(200),
   status CHARACTER VARYING(100) DEFAULT 'ACTIVE',
   date_start TIMESTAMP WITH TIME ZONE,
   date_finish TIMESTAMP WITH TIME ZONE,
   created TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
   updated TIMESTAMP WITH TIME ZONE  DEFAULT CURRENT_TIMESTAMP,
   UNIQUE(id)
);