CREATE TABLE users
(
    id           bigint primary key,
    state_of_user varchar,
    name_of_user varchar
);

CREATE TABLE orders
(
    id serial primary key ,
    id_of_user  bigint,
    number_of_order bigint,
    description varchar,
    foreign key (id_of_user) references users (id),
    unique (id_of_user, number_of_order)
);

CREATE TABLE photos_of_order
(
    id varchar primary key,
    id_of_order bigint,
    foreign key (id_of_order) references orders (id),
    unique (id)
);