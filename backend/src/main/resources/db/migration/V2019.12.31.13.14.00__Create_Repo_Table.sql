create table repo
(
    id               UUID PRIMARY KEY NOT NULL,
    user_id          uuid             not null,
    repo_name        text             not null,
    star_count       text             not null,
    owner_avatar_url text,
    tags             text[]
)
