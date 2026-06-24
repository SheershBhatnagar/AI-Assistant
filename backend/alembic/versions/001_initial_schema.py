"""Initial schema setup

Revision ID: 001_initial_schema
Revises: 
Create Date: 2026-06-24 12:00:00.000000

"""
from typing import Sequence, Union
from alembic import op
import sqlalchemy as sa
from sqlalchemy.dialects import postgresql

# revision identifiers, used by Alembic.
revision: str = '001_initial_schema'
down_revision: Union[str, None] = None
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    # 1. Ensure schemas exist (also handled in env.py)
    op.execute("CREATE SCHEMA IF NOT EXISTS users;")
    op.execute("CREATE SCHEMA IF NOT EXISTS chat;")
    op.execute("CREATE SCHEMA IF NOT EXISTS ai_config;")
    op.execute("CREATE SCHEMA IF NOT EXISTS system;")

    # 2. Create Custom Enum Type
    op.execute("CREATE TYPE chat.sender_type AS ENUM ('ai', 'user');")

    # 3. Create Users Accounts Table
    op.create_table(
        'accounts',
        sa.Column('id', postgresql.UUID(as_uuid=True), primary_key=True),
        sa.Column('email', sa.String(length=255), nullable=False, unique=True),
        sa.Column('first_name', sa.String(length=100), nullable=False),
        sa.Column('middle_name', sa.String(length=100), nullable=True),
        sa.Column('last_name', sa.String(length=100), nullable=True),
        sa.Column('_created_at', sa.DateTime(), nullable=False),
        sa.Column('_updated_at', sa.DateTime(), nullable=False),
        schema='users'
    )
    op.create_index('ix_users_accounts_email', 'accounts', ['email'], unique=True, schema='users')

    # 4. Create AI Models Table
    op.create_table(
        'models',
        sa.Column('id', postgresql.UUID(as_uuid=True), primary_key=True),
        sa.Column('user_id', postgresql.UUID(as_uuid=True), nullable=False),
        sa.Column('name', sa.String(length=100), nullable=False),
        sa.Column('api_key', sa.String(length=255), nullable=False, unique=True),
        sa.Column('_created_at', sa.DateTime(), nullable=False),
        sa.Column('_updated_at', sa.DateTime(), nullable=False),
        sa.ForeignKeyConstraint(['user_id'], ['users.accounts.id'], ondelete='CASCADE'),
        schema='ai_config'
    )

    # 5. Create Conversations Table
    op.create_table(
        'conversations',
        sa.Column('id', postgresql.UUID(as_uuid=True), primary_key=True),
        sa.Column('user_id', postgresql.UUID(as_uuid=True), nullable=False),
        sa.Column('title', sa.String(length=100), nullable=False),
        sa.Column('_created_at', sa.DateTime(), nullable=False),
        sa.Column('_updated_at', sa.DateTime(), nullable=False),
        sa.ForeignKeyConstraint(['user_id'], ['users.accounts.id'], ondelete='CASCADE'),
        schema='chat'
    )

    # 6. Create Messages Table
    op.create_table(
        'messages',
        sa.Column('id', postgresql.UUID(as_uuid=True), primary_key=True),
        sa.Column('user_id', postgresql.UUID(as_uuid=True), nullable=False),
        sa.Column('conversation_id', postgresql.UUID(as_uuid=True), nullable=False),
        sa.Column('model_id', postgresql.UUID(as_uuid=True), nullable=False),
        sa.Column('content', sa.Text(), nullable=False),
        sa.Column('sender_type', postgresql.ENUM('ai', 'user', name='sender_type', schema='chat', inherit_schema=True), nullable=False),
        sa.Column('_created_at', sa.DateTime(), nullable=False),
        sa.Column('_updated_at', sa.DateTime(), nullable=False),
        sa.ForeignKeyConstraint(['user_id'], ['users.accounts.id'], ondelete='CASCADE'),
        sa.ForeignKeyConstraint(['conversation_id'], ['chat.conversations.id'], ondelete='CASCADE'),
        sa.ForeignKeyConstraint(['model_id'], ['ai_config.models.id'], ondelete='RESTRICT'),
        schema='chat'
    )

    # 7. Create System Logs Table
    op.create_table(
        'logs',
        sa.Column('id', postgresql.UUID(as_uuid=True), primary_key=True),
        sa.Column('user_id', postgresql.UUID(as_uuid=True), nullable=False),
        sa.Column('action', sa.String(length=255), nullable=False),
        sa.Column('details', sa.Text(), nullable=False),
        sa.Column('_created_at', sa.DateTime(), nullable=False),
        sa.ForeignKeyConstraint(['user_id'], ['users.accounts.id'], ondelete='CASCADE'),
        schema='system'
    )

    # 8. Create Otps Table
    op.create_table(
        'otps',
        sa.Column('id', postgresql.UUID(as_uuid=True), primary_key=True),
        sa.Column('user_id', postgresql.UUID(as_uuid=True), nullable=False),
        sa.Column('otp', sa.String(length=10), nullable=False),
        sa.Column('expires_at', sa.DateTime(), nullable=False),
        sa.Column('is_used', sa.Boolean(), nullable=False, server_default='false'),
        sa.Column('_created_at', sa.DateTime(), nullable=False),
        sa.Column('_updated_at', sa.DateTime(), nullable=False),
        sa.ForeignKeyConstraint(['user_id'], ['users.accounts.id'], ondelete='CASCADE'),
        schema='users'
    )

    # 9. Create User Settings Table
    op.create_table(
        'user_settings',
        sa.Column('id', postgresql.UUID(as_uuid=True), primary_key=True),
        sa.Column('user_id', postgresql.UUID(as_uuid=True), nullable=False, unique=True),
        sa.Column('default_model_id', postgresql.UUID(as_uuid=True), nullable=False),
        sa.Column('_created_at', sa.DateTime(), nullable=False),
        sa.Column('_updated_at', sa.DateTime(), nullable=False),
        sa.ForeignKeyConstraint(['user_id'], ['users.accounts.id'], ondelete='CASCADE'),
        sa.ForeignKeyConstraint(['default_model_id'], ['ai_config.models.id'], ondelete='CASCADE'),
        schema='users'
    )


def downgrade() -> None:
    op.drop_table('user_settings', schema='users')
    op.drop_table('otps', schema='users')
    op.drop_table('logs', schema='system')
    op.drop_table('messages', schema='chat')
    op.drop_table('conversations', schema='chat')
    op.drop_table('models', schema='ai_config')
    op.drop_table('accounts', schema='users')
    op.execute("DROP TYPE chat.sender_type;")
