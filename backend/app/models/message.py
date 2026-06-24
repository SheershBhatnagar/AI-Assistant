import enum
import uuid
from datetime import datetime
from sqlalchemy import Column, String, DateTime, ForeignKey, Text
from sqlalchemy.dialects.postgresql import UUID, ENUM
from sqlalchemy.orm import relationship

from app.db.session import Base

class SenderType(str, enum.Enum):
    ai = "ai"
    user = "user"

class Message(Base):
    __tablename__ = "messages"
    __table_args__ = {"schema": "chat"}

    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    user_id = Column(UUID(as_uuid=True), ForeignKey("users.accounts.id", ondelete="CASCADE"), nullable=False)
    conversation_id = Column(UUID(as_uuid=True), ForeignKey("chat.conversations.id", ondelete="CASCADE"), nullable=False)
    model_id = Column(UUID(as_uuid=True), ForeignKey("ai_config.models.id", ondelete="RESTRICT"), nullable=False)
    content = Column(Text, nullable=False)
    sender_type = Column(
        ENUM(SenderType, name="sender_type", schema="chat", inherit_schema=True),
        nullable=False
    )
    _created_at = Column(DateTime, nullable=False, default=datetime.utcnow)
    _updated_at = Column(DateTime, nullable=False, default=datetime.utcnow, onupdate=datetime.utcnow)

    # Relationships
    user = relationship("User", back_populates="messages")
    conversation = relationship("Conversation", back_populates="messages")
