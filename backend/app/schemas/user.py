from typing import Optional
from pydantic import BaseModel, EmailStr

class RegisterUserRequest(BaseModel):
    email: EmailStr
    firstName: str
    middleName: Optional[str] = None
    lastName: Optional[str] = None

class UpdateProfileRequest(BaseModel):
    firstName: str
    lastName: Optional[str] = None

class UserResponse(BaseModel):
    id: str
    email: str
    firstName: str
    lastName: Optional[str] = None
    token: str

class UserProfileResponse(BaseModel):
    id: str
    email: str
    firstName: str
    lastName: Optional[str] = None
