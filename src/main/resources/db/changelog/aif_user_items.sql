create table if not exists aif_user_items (
    id                     bigserial
        constraint aif_user_items_pk primary key,
    aif_user_item_group_id bigint                  not null
        constraint aif_user_items_item_groups_fl references aif_user_item_groups,
    name                   text                    not null,
    hours                  int       default 0     not null,
    mins                   int       default 0     not null,
    amount                 decimal                 not null,
    active                 boolean   default false not null,
    created                timestamp default now() not null,
    file_data              bytea
);