create table ordering
(
    id        bigserial primary key,
    user_name varchar(200) not null,
    done boolean default false not null
);

create table ordering_items
(
    id          bigserial primary key,
    ordering_id bigint         not null
        references ordering (id),
    item_name   varchar(200)   not null,
    item_count  int            not null,
    item_price  numeric(10, 2) not null
);
create index order_items__order_id on ordering_items (ordering_id);
