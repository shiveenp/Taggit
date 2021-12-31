create table repo
(
    id                 uuid PRIMARY KEY,
    repo_id            bigint not null,
    repo_name          text   not null,
    github_link        text   not null,
    github_description text,
    star_count         int    not null,
    owner_avatar_url   text,
    metadata               jsonb
);
