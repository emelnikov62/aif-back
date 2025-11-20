create table if not exists aif_user_staff_items (
    id                bigserial
        constraint aif_user_staff_items_pk primary key,
    aif_user_staff_id bigint                  not null
        constraint aif_user_staff_items_user_staffs_fk references aif_user_staffs,
    aif_user_item_id  bigint                  not null
        constraint aif_user_staff_items_user_items_fk references aif_user_items,
    created           timestamp default now() not null
);

create index if not exists aif_user_staff_items_user_staff_id_idx on aif_user_staff_items(aif_user_staff_id);
create index if not exists aif_user_staff_items_user_item_id_idx on aif_user_staff_items(aif_user_item_id);

alter table aif_user_staff_items add if not exists active boolean default true not null;
create index if not exists aif_user_staff_items_acitve_idx on aif_user_staff_items(active);