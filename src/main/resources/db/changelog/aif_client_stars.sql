create table if not exists aif_client_stars (
    id               bigserial
        constraint aif_client_stars_pk primary key,
    aif_client_id        bigint                     not null
        constraint aif_client_stars_clients_fk references aif_clients,
    aif_staff_id     bigint                  not null
        constraint aif_client_stars_staffs_fk references aif_user_staffs,
    aif_user_bot_id  bigint                  not null
        constraint aif_client_stars_user_bots_fk references aif_user_bots,
    aif_user_item_id bigint                  not null
        constraint aif_client_stars_user_items_fk references aif_user_items,
    value            int                     not null,
    created          timestamp default now() not null
);

create index if not exists aif_client_stars_staff_idx on aif_client_stars(aif_staff_id);
create index if not exists aif_client_stars_client_idx on aif_client_stars(aif_client_id);
create index if not exists aif_client_stars_user_bot_idx on aif_client_stars(aif_user_bot_id);
create index if not exists aif_client_stars_user_item_idx on aif_client_stars(aif_user_item_id);
create index if not exists aif_client_stars_value_idx on aif_client_stars(value);