-- V3__Add_UserSettings_Table.sql

CREATE TABLE users.user_settings (
    _created_at TIMESTAMP NOT NULL,
    _updated_at TIMESTAMP NOT NULL,
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users.accounts(id) ON DELETE CASCADE,
    default_model_id UUID NOT NULL REFERENCES ai_config.models(id) ON DELETE CASCADE
);
