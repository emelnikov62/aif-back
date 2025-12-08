create table if not exists aif_client_records (
    id                   bigserial
        constraint aif_client_records_pk primary key,
    aif_client_id        bigint                     not null
        constraint aif_client_records_clients_fk references aif_clients,
    aif_user_bot_id      bigint                     not null
        constraint aif_client_records_user_bots_fk references aif_user_bots,
    aif_user_item_id     bigint                     not null
        constraint aif_client_records_user_items_fk references aif_user_items,
    aif_user_calendar_id bigint                     not null
        constraint aif_client_records_user_calendar_fk references aif_user_calendar,
    aif_user_staff_id bigint
        constraint aif_client_records_user_staffs_fl references aif_user_staffs,
    hours                int       default 0        not null,
    mins                 int       default 0        not null,
    status               text      default 'active' not null,
    created              timestamp default now()    not null
);

create index if not exists aif_client_records_client_idx on aif_client_records(aif_client_id);
create index if not exists aif_client_records_user_bot_idx on aif_client_records(aif_user_bot_id);
create index if not exists aif_client_records_user_item_idx on aif_client_records(aif_user_item_id);
create index if not exists aif_client_records_user_calendar_idx on aif_client_records(aif_user_calendar_id);
create index if not exists aif_client_records_hours_idx on aif_client_records(hours);
create index if not exists aif_client_records_mins_idx on aif_client_records(mins);
create index if not exists aif_client_records_status_idx on aif_client_records(status);
create index if not exists aif_client_records_staff_idx on aif_client_records(aif_user_staff_id);