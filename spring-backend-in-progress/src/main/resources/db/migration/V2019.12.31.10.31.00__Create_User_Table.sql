create table users (
    id UUID PRIMARY KEY,
    user_name text not null,
    email text,
    avatar_url text,
    github_user_name text not null,
    github_user_id bigint not null unique,
    created_at timestamptz not null,
    updated_at timestamptz not null
);
