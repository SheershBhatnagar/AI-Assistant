import pytest
from unittest.mock import MagicMock
from sqlalchemy.orm import Session

from app.db.base import Base, User
from app.services.user_service import UserService

def test_register_user_success():
    # Arrange
    db_mock = MagicMock(spec=Session)
    user_service = UserService()
    
    # Mock database queries
    db_mock.query().filter().first.return_value = None  # unique email check passes
    
    # Act
    new_user = user_service.register_user(
        db=db_mock,
        email="tony@stark.com",
        first_name="Tony",
        last_name="Stark"
    )
    
    # Assert
    assert new_user is not None
    assert new_user.email == "tony@stark.com"
    assert new_user.first_name == "Tony"
    assert new_user.last_name == "Stark"
    db_mock.add.assert_called_once()
    db_mock.commit.assert_called_once()

def test_register_user_duplicate_email():
    # Arrange
    db_mock = MagicMock(spec=Session)
    user_service = UserService()
    
    # Mock existing user returning a record
    db_mock.query().filter().first.return_value = User(
        email="tony@stark.com",
        first_name="Existing"
    )
    
    # Act & Assert
    with pytest.raises(ValueError) as excinfo:
        user_service.register_user(
            db=db_mock,
            email="tony@stark.com",
            first_name="Imposter"
        )
    
    assert "A user with this email already exists." in str(excinfo.value)
    db_mock.add.assert_not_called()
