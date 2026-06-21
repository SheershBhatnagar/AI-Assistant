-- V2__Add_Otps_Table.sql

CREATE TABLE users.otps (
    _created_at TIMESTAMP NOT NULL,
    _updated_at TIMESTAMP NOT NULL,
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users.accounts(id) ON DELETE CASCADE,
    otp VARCHAR(10) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_used BOOLEAN NOT NULL DEFAULT FALSE
);
