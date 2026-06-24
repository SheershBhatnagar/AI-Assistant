import uuid
import pytest
from unittest.mock import MagicMock
from sqlalchemy.orm import Session

from app.services.user_settings_service import UserSettingsService
from app.db.base import Base, UserSettings, AiModel

def test_get_user_settings():
    db_mock = MagicMock(spec=Session)
    service = UserSettingsService()
    
    user_id = uuid.uuid4()
    expected_settings = UserSettings(
        id=uuid.uuid4(),
        user_id=user_id,
        default_model_id=uuid.uuid4()
    )
    db_mock.query().filter().first.return_value = expected_settings
    
    result = service.get_user_settings(db_mock, str(user_id))
    
    assert result is not None
    assert result.user_id == user_id
    assert result.default_model_id == expected_settings.default_model_id

def test_update_default_model_success():
    db_mock = MagicMock(spec=Session)
    service = UserSettingsService()
    
    user_id = str(uuid.uuid4())
    model_id = str(uuid.uuid4())
    
    existing_model = AiModel(
        id=uuid.UUID(model_id),
        user_id=uuid.UUID(user_id),
        name="gemini-1.5-flash",
        api_key="api-key"
    )
    
    # 1. Mock finding the model
    # 2. Mock finding settings (return None, i.e. create new settings)
    db_mock.query().filter().first.side_effect = [existing_model, None]
    
    result = service.update_default_model(db_mock, user_id, model_id)
    
    assert result is not None
    assert str(result.default_model_id) == model_id
    assert str(result.user_id) == user_id
    db_mock.add.assert_called_once()
    db_mock.commit.assert_called_once()

def test_update_default_model_not_found():
    db_mock = MagicMock(spec=Session)
    service = UserSettingsService()
    
    user_id = str(uuid.uuid4())
    model_id = str(uuid.uuid4())
    
    # Mock finding model returns None
    db_mock.query().filter().first.return_value = None
    
    with pytest.raises(ValueError) as excinfo:
        service.update_default_model(db_mock, user_id, model_id)
        
    assert "Model configuration not found" in str(excinfo.value)
    db_mock.commit.assert_not_called()

def test_update_default_model_belongs_to_other_user():
    db_mock = MagicMock(spec=Session)
    service = UserSettingsService()
    
    user_id = str(uuid.uuid4())
    stranger_id = str(uuid.uuid4())
    model_id = str(uuid.uuid4())
    
    stranger_model = AiModel(
        id=uuid.UUID(model_id),
        user_id=uuid.UUID(stranger_id),
        name="gemini-1.5-flash",
        api_key="api-key"
    )
    
    # Mock finding the model
    db_mock.query().filter().first.return_value = stranger_model
    
    with pytest.raises(ValueError) as excinfo:
        service.update_default_model(db_mock, user_id, model_id)
        
    assert "Model configuration does not belong to the user" in str(excinfo.value)
    db_mock.commit.assert_not_called()
