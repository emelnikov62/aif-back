create table if not exists aif_user_bots (
    id          bigserial
        constraint aif_user_bots_pk primary key,
    aif_user_id bigint                  not null
        constraint aif_user_bots_aif_users_fk
            references aif_users,
    aif_bot_id  bigint                  not null
        constraint aif_user_bots_aif_bots_fk
            references aif_bots,
    source      text                    not null,
    token       text,
    active      boolean   default false not null,
    created     timestamp default now() not null
);

create index if not exists aif_user_bots_source_idx on aif_user_bots(source);

