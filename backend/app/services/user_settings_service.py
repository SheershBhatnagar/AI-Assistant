from datetime import datetime
from typing import Optional
from sqlalchemy.orm import Session
from app.models.user_settings import UserSettings
from app.models.ai_model import AiModel

class UserSettingsService:
    def get_user_settings(self, db: Session, user_id: str) -> Optional[UserSettings]:
        return db.query(UserSettings).filter(UserSettings.user_id == user_id).first()

    def update_default_model(self, db: Session, user_id: str, default_model_id: str) -> UserSettings:
        # Check model exists
        model = db.query(AiModel).filter(AiModel.id == default_model_id).first()
        if not model:
            raise ValueError("Model configuration not found")

        # Security check: Model belongs to the user
        if str(model.user_id) != user_id:
            raise ValueError("Model configuration does not belong to the user")

        settings = db.query(UserSettings).filter(UserSettings.user_id == user_id).first()
        now = datetime.utcnow()

        if settings:
            settings.default_model_id = default_model_id
            settings._updated_at = now
        else:
            settings = UserSettings(
                user_id=user_id,
                default_model_id=default_model_id,
                _created_at=now,
                _updated_at=now
            )
            db.add(settings)

        db.commit()
        db.refresh(settings)
        return settings
