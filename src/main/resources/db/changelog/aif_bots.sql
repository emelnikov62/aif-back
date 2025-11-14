create table if not exists aif_bots (
    id          bigserial
        constraint aif_bots_pk primary key,
    type        text                    not null,
    description text                    not null,
    active      boolean   default false not null,
    created     timestamp default now() not null
);

