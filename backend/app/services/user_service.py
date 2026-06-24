from datetime import datetime
from typing import Optional
from sqlalchemy.orm import Session
from app.models.user import User
from app.utils.jwt_helper import generate_jwt_token as create_jwt

class UserService:
    def register_user(self, db: Session, email: str, first_name: str, middle_name: Optional[str] = None, last_name: Optional[str] = None) -> User:
        existing_user = db.query(User).filter(User.email == email).first()
        if existing_user:
            raise ValueError("A user with this email already exists.")

        new_user = User(
            email=email,
            first_name=first_name,
            middle_name=middle_name,
            last_name=last_name
        )
        db.add(new_user)
        db.commit()
        db.refresh(new_user)
        return new_user

    def get_user_by_id(self, db: Session, user_id: str) -> Optional[User]:
        return db.query(User).filter(User.id == user_id).first()

    def update_user(self, db: Session, user_id: str, first_name: str, last_name: Optional[str] = None) -> Optional[User]:
        user = db.query(User).filter(User.id == user_id).first()
        if not user:
            return None

        user.first_name = first_name
        user.last_name = last_name
        user._updated_at = datetime.utcnow()
        db.commit()
        db.refresh(user)
        return user

    def generate_jwt_token(self, user_id: str) -> str:
        return create_jwt(user_id)
