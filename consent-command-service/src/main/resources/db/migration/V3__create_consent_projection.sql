CREATE TABLE consent_projection (
        id              BIGSERIAL       PRIMARY KEY,
        user_id         BIGINT          NOT NULL,
        data_category   VARCHAR(100)    NOT NULL,
        finality        VARCHAR(100)    NOT NULL,
        status          VARCHAR(50)     NOT NULL,
        legal_basis     VARCHAR(100)    NOT NULL,
        granted_at      TIMESTAMPTZ,
        revoked_at      TIMESTAMPTZ,
        last_event_id   BIGINT          NOT NULL REFERENCES event(id),
        version         BIGINT          NOT NULL,
        updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
        CONSTRAINT uq_consent_projection UNIQUE (user_id, data_category, finality)
);

CREATE INDEX idx_projection_user_id       ON consent_projection (user_id);
CREATE INDEX idx_projection_status        ON consent_projection (status);
CREATE INDEX idx_projection_user_status   ON consent_projection (user_id, status);