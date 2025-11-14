create table if not exists aif_users (
    id      bigserial
        constraint aif_users_pk primary key,
    tg_id   text                    not null
        constraint aif_users_unique unique,
    active  boolean   default true  not null,
    created timestamp default now() not null
);

