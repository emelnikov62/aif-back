create table if not exists aif_users (
    id        bigserial
        constraint aif_users_pk primary key,
    source_id text                    not null
        constraint aif_users_unique unique,
    active    boolean   default true  not null,
    created   timestamp default now() not null,
    source    text                    not null
);

create index if not exists aif_users_source_id on aif_users(source_id);
create index if not exists aif_users_source_idx on aif_users(source);

