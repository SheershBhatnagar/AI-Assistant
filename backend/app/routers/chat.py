from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List

from app.routers.deps import get_db, get_current_user_id
from app.schemas.chat import (
    CreateConversationRequest,
    ConversationResponse,
    SendMessageRequest,
    MessageResponse
)
from app.services.chat_service import ChatService

router = APIRouter(prefix="/api/v1/chat", tags=["chat"])
chat_service = ChatService()

@router.post("/conversations", response_model=ConversationResponse, status_code=status.HTTP_201_CREATED)
def create_conversation(
    request: CreateConversationRequest,
    user_id: str = Depends(get_current_user_id),
    db: Session = Depends(get_db)
):
    try:
        conv = chat_service.start_conversation(db, user_id, request.title)
        return ConversationResponse(
            id=str(conv.id),
            title=conv.title,
            createdAt=str(conv._created_at)
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=str(e)
        )

@router.post("/messages", response_model=MessageResponse, status_code=status.HTTP_201_CREATED)
async def send_message(
    request: SendMessageRequest,
    user_id: str = Depends(get_current_user_id),
    db: Session = Depends(get_db)
):
    try:
        ai_message = await chat_service.process_user_message(
            db=db,
            user_id=user_id,
            conversation_id=request.conversationId,
            model_id=request.modelId,
            content=request.content,
            sender_type=request.senderType
        )
        return MessageResponse(
            id=str(ai_message.id),
            senderType=ai_message.sender_type,
            content=ai_message.content,
            createdAt=str(ai_message._created_at)
        )
    except ValueError as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=str(e)
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=str(e)
        )

@router.get("/conversations", response_model=List[ConversationResponse], status_code=status.HTTP_200_OK)
def get_conversations(
    user_id: str = Depends(get_current_user_id),
    db: Session = Depends(get_db)
):
    try:
        conversations = chat_service.get_user_conversations(db, user_id)
        return [
            ConversationResponse(
                id=str(c.id),
                title=c.title,
                createdAt=str(c._created_at)
            ) for c in conversations
        ]
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=str(e)
        )

@router.get("/conversations/{conversation_id}/messages", response_model=List[MessageResponse], status_code=status.HTTP_200_OK)
def get_messages(
    conversation_id: str,
    user_id: str = Depends(get_current_user_id),
    db: Session = Depends(get_db)
):
    try:
        messages = chat_service.get_conversation_history(db, conversation_id)
        return [
            MessageResponse(
                id=str(m.id),
                senderType=m.sender_type,
                content=m.content,
                createdAt=str(m._created_at)
            ) for m in messages
        ]
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=str(e)
        )
