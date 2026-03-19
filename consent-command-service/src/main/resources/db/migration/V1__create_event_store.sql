CREATE TABLE event (
       id            BIGSERIAL       PRIMARY KEY,
       stream_id     VARCHAR(255)    NOT NULL,
       version       BIGINT          NOT NULL,
       user_id       BIGINT          NOT NULL,
       event_type    VARCHAR(100)    NOT NULL,
       data_category VARCHAR(100)    NOT NULL,
       finality      VARCHAR(100)    NOT NULL,
       payload       JSONB           NOT NULL,
       issued_by     JSONB           NOT NULL,
       occurred_at   TIMESTAMPTZ     NOT NULL,
       created_at    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
       CONSTRAINT uq_event_stream_version UNIQUE (stream_id, version)
);
