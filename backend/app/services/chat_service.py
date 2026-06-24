from datetime import datetime
from typing import List, Optional
from sqlalchemy.orm import Session

from app.models.conversation import Conversation
from app.models.message import Message, SenderType
from app.models.user_settings import UserSettings
from app.models.ai_model import AiModel
from app.utils.ai_client import AiClient

class ChatService:
    def __init__(self, ai_client: Optional[AiClient] = None):
        self.ai_client = ai_client or AiClient()

    def start_conversation(self, db: Session, user_id: str, title: str) -> Conversation:
        new_conv = Conversation(
            user_id=user_id,
            title=title
        )
        db.add(new_conv)
        db.commit()
        db.refresh(new_conv)
        return new_conv

    def get_user_conversations(self, db: Session, user_id: str) -> List[Conversation]:
        return db.query(Conversation).filter(
            Conversation.user_id == user_id
        ).order_by(Conversation._created_at.desc()).all()

    def get_conversation_history(self, db: Session, conversation_id: str) -> List[Message]:
        return db.query(Message).filter(
            Message.conversation_id == conversation_id
        ).order_by(Message._created_at.asc()).all()

    async def process_user_message(
        self,
        db: Session,
        user_id: str,
        conversation_id: str,
        model_id: Optional[str],
        content: str,
        sender_type: SenderType
    ) -> Message:
        if not content or content.strip() == "":
            raise ValueError("Message content cannot be empty.")

        # 1. Resolve default model if not supplied
        resolved_model_id = model_id
        if not resolved_model_id:
            settings = db.query(UserSettings).filter(UserSettings.user_id == user_id).first()
            if not settings:
                raise ValueError("No default model configured, and no model ID was provided in the message request.")
            resolved_model_id = str(settings.default_model_id)

        # 2. Save user message to DB
        user_msg = Message(
            user_id=user_id,
            conversation_id=conversation_id,
            model_id=resolved_model_id,
            content=content,
            sender_type=sender_type
        )
        db.add(user_msg)
        db.commit()
        db.refresh(user_msg)

        # 3. Lookup the Model Configuration
        config = db.query(AiModel).filter(AiModel.id == resolved_model_id).first()
        if not config:
            raise ValueError("Model config not found")

        # 4. Call the correct AI Provider
        model_name_lower = config.name.lower()
        if "gemini" in model_name_lower:
            ai_response_content = await self.ai_client.call_gemini(config.name, config.api_key, content)
        elif "gpt" in model_name_lower:
            ai_response_content = await self.ai_client.call_openai(config.name, config.api_key, content)
        else:
            ai_response_content = "Unknown model provider"

        # 5. Create and Save the AI's response message
        ai_msg = Message(
            user_id=user_id,
            conversation_id=conversation_id,
            model_id=resolved_model_id,
            content=ai_response_content,
            sender_type=SenderType.ai
        )
        db.add(ai_msg)
        db.commit()
        db.refresh(ai_msg)

        return ai_msg
