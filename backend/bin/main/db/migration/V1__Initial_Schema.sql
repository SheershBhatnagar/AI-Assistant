-- V1__Initial_Schema.sql

-- 1. Create the schemas
CREATE SCHEMA IF NOT EXISTS users;
CREATE SCHEMA IF NOT EXISTS chat;
CREATE SCHEMA IF NOT EXISTS ai_config;
CREATE SCHEMA IF NOT EXISTS system;

-- 2. Create Custom Enums
CREATE TYPE chat.sender_type AS ENUM ('ai', 'user');

-- 3. Users Table
CREATE TABLE users.accounts (
    _created_at TIMESTAMP NOT NULL,
    _updated_at TIMESTAMP NOT NULL,
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100)
);

-- 4. AI Models Table
CREATE TABLE ai_config.models (
    _created_at TIMESTAMP NOT NULL,
    _updated_at TIMESTAMP NOT NULL,
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users.accounts(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    api_key VARCHAR(255) UNIQUE NOT NULL
);

-- 5. Conversations Table
CREATE TABLE chat.conversations (
    _created_at TIMESTAMP NOT NULL,
    _updated_at TIMESTAMP NOT NULL,
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users.accounts(id) ON DELETE CASCADE,
    title VARCHAR(100) NOT NULL
);

-- 6. Messages Table
CREATE TABLE chat.messages (
    _created_at TIMESTAMP NOT NULL,
    _updated_at TIMESTAMP NOT NULL,
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users.accounts(id) ON DELETE CASCADE,
    conversation_id UUID NOT NULL REFERENCES chat.conversations(id) ON DELETE CASCADE,
    model_id UUID NOT NULL REFERENCES ai_config.models(id) ON DELETE RESTRICT,
    content TEXT NOT NULL,
    sender_type chat.sender_type NOT NULL
);

-- 7. System Logs Table
CREATE TABLE system.logs (
    _created_at TIMESTAMP NOT NULL,
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users.accounts(id) ON DELETE CASCADE,
    action VARCHAR(255) NOT NULL,
    details TEXT NOT NULL
);
