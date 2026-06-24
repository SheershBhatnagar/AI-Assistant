from typing import List, Optional
from sqlalchemy.orm import Session
from app.models.ai_model import AiModel

class AiModelService:
    def add_model_config(self, db: Session, user_id: str, name: str, api_key: str) -> AiModel:
        if not api_key or api_key.strip() == "":
            raise ValueError("API Key cannot be blank.")

        new_model = AiModel(
            user_id=user_id,
            name=name,
            api_key=api_key
        )
        db.add(new_model)
        db.commit()
        db.refresh(new_model)
        return new_model

    def get_user_models(self, db: Session, user_id: str) -> List[AiModel]:
        return db.query(AiModel).filter(AiModel.user_id == user_id).all()

    def get_model_details(self, db: Session, model_id: str) -> Optional[AiModel]:
        return db.query(AiModel).filter(AiModel.id == model_id).first()
