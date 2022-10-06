create table affiliate_marketer if not exists
(
    id           bigint not null
        primary key,
    email        varchar(255),
    first_name   varchar(255),
    last_name    varchar(255),
    phone_number varchar(255),
    referal_code varchar(255)
);

alter table affiliate_marketer
    owner to postgres;

