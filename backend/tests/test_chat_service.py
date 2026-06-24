import uuid
import pytest
from unittest.mock import MagicMock, AsyncMock
from sqlalchemy.orm import Session

from app.services.chat_service import ChatService
from app.db.base import Base, SenderType, Message, UserSettings, AiModel
from app.utils.ai_client import AiClient

@pytest.mark.asyncio
async def test_process_user_message_resolves_default_model():
    # Arrange
    db_mock = MagicMock(spec=Session)
    ai_client_mock = MagicMock(spec=AiClient)
    ai_client_mock.call_gemini = AsyncMock(return_value="Kotlin is awesome!")
    
    chat_service = ChatService(ai_client=ai_client_mock)
    
    user_id = str(uuid.uuid4())
    conversation_id = str(uuid.uuid4())
    default_model_id = uuid.uuid4()
    
    user_settings = UserSettings(
        id=uuid.uuid4(),
        user_id=uuid.UUID(user_id),
        default_model_id=default_model_id
    )
    
    mock_model_details = AiModel(
        id=default_model_id,
        user_id=uuid.UUID(user_id),
        name="gemini-1.5-flash",
        api_key="api-key-xyz"
    )
    
    # query().filter().first() mock queries:
    # 1. user settings fetch (returns user_settings)
    # 2. model details fetch (returns mock_model_details)
    db_mock.query().filter().first.side_effect = [user_settings, mock_model_details]
    
    # Act
    result = await chat_service.process_user_message(
        db=db_mock,
        user_id=user_id,
        conversation_id=conversation_id,
        model_id=None,  # Null modelId, triggers fallback
        content="What is Kotlin?",
        sender_type=SenderType.user
    )
    
    # Assert
    assert result is not None
    assert result.sender_type == SenderType.ai
    assert result.content == "Kotlin is awesome!"
    assert str(result.model_id) == str(default_model_id)
    
    # Verify save calls: user message and AI message both saved (db.add + db.commit called twice)
    assert db_mock.add.call_count == 2
    assert db_mock.commit.call_count == 2
    ai_client_mock.call_gemini.assert_awaited_once_with("gemini-1.5-flash", "api-key-xyz", "What is Kotlin?")

@pytest.mark.asyncio
async def test_process_user_message_no_default_model_configured():
    # Arrange
    db_mock = MagicMock(spec=Session)
    chat_service = ChatService()
    
    user_id = str(uuid.uuid4())
    conversation_id = str(uuid.uuid4())
    
    # query().filter().first() returns None for settings
    db_mock.query().filter().first.return_value = None
    
    # Act & Assert
    with pytest.raises(ValueError) as excinfo:
        await chat_service.process_user_message(
            db=db_mock,
            user_id=user_id,
            conversation_id=conversation_id,
            model_id=None,
            content="Hi",
            sender_type=SenderType.user
        )
        
    assert "No default model configured" in str(excinfo.value)
    db_mock.add.assert_not_called()
    db_mock.commit.assert_not_called()
