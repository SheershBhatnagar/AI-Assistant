from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.routers.deps import get_db
from app.schemas.auth import SendOtpRequest, VerifyOtpRequest, AuthResponse
from app.services.auth_service import AuthService

router = APIRouter(prefix="/api/v1/auth", tags=["auth"])
auth_service = AuthService()

@router.post("/send-otp", status_code=status.HTTP_200_OK)
def send_otp(request: SendOtpRequest, db: Session = Depends(get_db)):
    try:
        auth_service.send_otp(db, str(request.email))
        return {"message": f"OTP sent successfully to {request.email}"}
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=str(e)
        )

@router.post("/verify-otp", response_model=AuthResponse, status_code=status.HTTP_200_OK)
def verify_otp(request: VerifyOtpRequest, db: Session = Depends(get_db)):
    try:
        jwt_token = auth_service.verify_otp_and_login(db, str(request.email), request.otp)
        return AuthResponse(
            token=jwt_token,
            message="Authentication successful"
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
