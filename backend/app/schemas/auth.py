from pydantic import BaseModel, EmailStr

class SendOtpRequest(BaseModel):
    email: EmailStr

class VerifyOtpRequest(BaseModel):
    email: EmailStr
    otp: str

class AuthResponse(BaseModel):
    token: str
    message: str
