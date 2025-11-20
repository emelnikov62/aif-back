create table if not exists aif_user_calendar (
    id              bigserial
        constraint aif_user_calendar_pk primary key,
    aif_user_bot_id bigint                  not null
        constraint aif_user_calendar_user_bots_fk references aif_user_bots,
    hours_start     int       default 0     not null,
    mins_start      int       default 0     not null,
    hours_end       int       default 0     not null,
    mins_end        int       default 0     not null,
    day             int                     not null,
    month           int                     not null,
    year            int                     not null,
    created         timestamp default now() not null
);

create index if not exists aif_user_calendar_month_idx on aif_user_calendar(month);
create index if not exists aif_user_calendar_year_idx on aif_user_calendar(year);
create index if not exists aif_user_calendar_aubi_idx on aif_user_calendar(aif_user_bot_id);
create index if not exists aif_user_calendar_day_idx on aif_user_calendar(day);