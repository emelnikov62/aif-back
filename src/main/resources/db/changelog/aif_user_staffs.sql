create table if not exists aif_user_staffs (
    id              bigserial
        constraint aif_user_staffs_pk primary key,
    aif_user_bot_id bigint                  not null
        constraint aif_user_staffs_user_bots_fk references aif_user_bots,
    name            text                    not null,
    surname         text                    not null,
    third           text                    not null,
    active          boolean   default false not null,
    created         timestamp default now() not null
);

create index if not exists aif_user_staffs_user_bot_id_idx on aif_user_staffs(aif_user_bot_id);