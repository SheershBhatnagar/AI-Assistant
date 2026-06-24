from typing import Optional
from pydantic import BaseModel
from app.models.message import SenderType

class CreateConversationRequest(BaseModel):
    title: str

class SendMessageRequest(BaseModel):
    conversationId: str
    modelId: Optional[str] = None
    content: str
    senderType: SenderType

class ConversationResponse(BaseModel):
    id: str
    title: Optional[str]
    createdAt: str

class MessageResponse(BaseModel):
    id: str
    senderType: SenderType
    content: str
    createdAt: str
