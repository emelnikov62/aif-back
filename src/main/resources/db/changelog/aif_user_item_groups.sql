create table if not exists aif_user_item_groups (
    id              bigserial
        constraint aif_user_item_groups_pk primary key,
    aif_user_bot_id bigint                  not null
        constraint aif_user_item_groups_user_bots_fk references aif_user_bots,
    name            text                    not null,
    active          boolean   default false not null,
    created         timestamp default now() not null
);