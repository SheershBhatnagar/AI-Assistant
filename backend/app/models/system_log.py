import uuid
from datetime import datetime
from sqlalchemy import Column, String, DateTime, ForeignKey, Text
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import relationship

from app.db.session import Base

class SystemLog(Base):
    __tablename__ = "logs"
    __table_args__ = {"schema": "system"}

    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    user_id = Column(UUID(as_uuid=True), ForeignKey("users.accounts.id", ondelete="CASCADE"), nullable=False)
    action = Column(String(255), nullable=False)
    details = Column(Text, nullable=False)
    _created_at = Column(DateTime, nullable=False, default=datetime.utcnow)

    # Relationships
    user = relationship("User", back_populates="logs")
