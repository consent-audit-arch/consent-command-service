CREATE TABLE outbox (
        id            BIGSERIAL       PRIMARY KEY,
        stream_id     VARCHAR(255)    NOT NULL,
        event_type    VARCHAR(100)    NOT NULL,
        topic         VARCHAR(255)    NOT NULL,
        payload       TEXT            NOT NULL,
        published     BOOLEAN         NOT NULL DEFAULT FALSE,
        created_at    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
        published_at  TIMESTAMPTZ
);

CREATE INDEX idx_outbox_published ON outbox (published) WHERE published = FALSE;
