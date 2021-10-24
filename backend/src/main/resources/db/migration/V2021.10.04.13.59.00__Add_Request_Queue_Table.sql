create table request_queue(
    id uuid primary key,
    user_id uuid not null,
    type text not null,
    payload text,
    status text not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);
