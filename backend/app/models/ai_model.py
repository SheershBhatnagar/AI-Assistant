import uuid
from datetime import datetime
from sqlalchemy import Column, String, DateTime, ForeignKey
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import relationship

from app.db.session import Base

class AiModel(Base):
    __tablename__ = "models"
    __table_args__ = {"schema": "ai_config"}

    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    user_id = Column(UUID(as_uuid=True), ForeignKey("users.accounts.id", ondelete="CASCADE"), nullable=False)
    name = Column(String(100), nullable=False)
    api_key = Column(String(255), unique=True, nullable=False)
    _created_at = Column(DateTime, nullable=False, default=datetime.utcnow)
    _updated_at = Column(DateTime, nullable=False, default=datetime.utcnow, onupdate=datetime.utcnow)

    # Relationships
    user = relationship("User", back_populates="models")
    settings = relationship("UserSettings", back_populates="default_model", cascade="all, delete-orphan")
