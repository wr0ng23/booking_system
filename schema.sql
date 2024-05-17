CREATE TABLE users
(
    id            bigint primary key,
    state_of_user varchar,
    name_of_user  varchar
);

CREATE TABLE orders
(
    id          serial primary key,
    id_of_user  bigint,
    description varchar,
    city        varchar,
    address     varchar,
    latitude    float8,
    longitude   float8,
    state       varchar,
    foreign key (id_of_user) references users (id),
    unique (id_of_user, number_of_order)
);

CREATE TABLE photos_of_order
(
    id          varchar primary key,
    id_of_order bigint,
    foreign key (id_of_order) references orders (id),
    unique (id)
);

CREATE TABLE booking
(
    id          serial primary key,
    id_of_order bigint,
    id_of_user  bigint,
    date_start  date,
    date_end    date,
    status      varchar,
    foreign key (id_of_order) references orders (id),
    foreign key (id_of_user) references users (id)
);

CREATE table metro_info
(
    id        serial primary key,
    name      varchar,
    city      varchar,
    longitude double precision,
    latitude  double precision,
    line_id   varchar,
    unique (name, city)
);

CREATE table metro_distance_order
(
    id       serial primary key,
    id_metro int,
    id_order int,
    distance double precision,
    foreign key (id_metro) references metro_info (id),
    foreign key (id_order) references orders (id)
);

ALTER TABLE booking
    ADD CONSTRAINT unique_records UNIQUE (id_of_order, id_of_user, date_start, date_end);


create table orders_in_message
(
    id            serial primary key,
    id_of_message int,
    id_of_order   int,
    user_id       int,
    unique (id_of_message, id_of_order, user_id),
    foreign key (id_of_order) references orders (id),
    foreign key (user_id) references users (id)
);