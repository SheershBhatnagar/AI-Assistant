import uuid
from datetime import datetime
from sqlalchemy import Column, String, DateTime, ForeignKey
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import relationship

from app.db.session import Base

class Conversation(Base):
    __tablename__ = "conversations"
    __table_args__ = {"schema": "chat"}

    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    user_id = Column(UUID(as_uuid=True), ForeignKey("users.accounts.id", ondelete="CASCADE"), nullable=False)
    title = Column(String(100), nullable=False)
    _created_at = Column(DateTime, nullable=False, default=datetime.utcnow)
    _updated_at = Column(DateTime, nullable=False, default=datetime.utcnow, onupdate=datetime.utcnow)

    # Relationships
    user = relationship("User", back_populates="conversations")
    messages = relationship("Message", back_populates="conversation", cascade="all, delete-orphan")
