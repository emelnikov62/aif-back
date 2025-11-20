create table if not exists aif_clients (
    id      bigserial
        constraint aif_clients_pk primary key,
    tg_id   text                    not null
        constraint aif_clients_unique unique,
    active  boolean   default true  not null,
    created timestamp default now() not null
);

