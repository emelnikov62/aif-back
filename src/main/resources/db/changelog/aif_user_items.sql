create table if not exists aif_user_items (
    id              bigserial
        constraint aif_user_items_pk primary key,
    aif_user_bot_id bigint                  not null
        constraint aif_user_items_user_bots_fk references aif_user_bots,
    name            text                    not null,
    hours           int       default 0     not null,
    mins            int       default 0     not null,
    amount          decimal                 not null,
    active          boolean   default false not null,
    created         timestamp default now() not null
);