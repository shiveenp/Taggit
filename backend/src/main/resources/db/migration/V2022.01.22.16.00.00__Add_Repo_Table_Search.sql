ALTER TABLE repo ADD column repo_name_ts tsvector
GENERATED ALWAYS AS (to_tsvector('english', repo_name)) STORED;

CREATE INDEX repo_name_ts_idx ON repo USING GIN(repo_name_ts);

ALTER TABLE repo ADD column github_description_ts tsvector
    GENERATED ALWAYS AS (to_tsvector('english', github_description)) STORED;

CREATE INDEX github_description_ts_idx ON repo USING GIN(github_description_ts);