from typing import List
from sqlalchemy.orm import Session
from app.models.system_log import SystemLog

class SystemLogService:
    def log_action(self, db: Session, user_id: str, action: str, details: str) -> SystemLog:
        new_log = SystemLog(
            user_id=user_id,
            action=action,
            details=details
        )
        db.add(new_log)
        db.commit()
        db.refresh(new_log)
        return new_log

    def get_user_logs(self, db: Session, user_id: str) -> List[SystemLog]:
        return db.query(SystemLog).filter(
            SystemLog.user_id == user_id
        ).order_by(SystemLog._created_at.desc()).all()
