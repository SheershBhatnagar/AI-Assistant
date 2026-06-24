from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.routers.deps import get_db, get_current_user_id
from app.schemas.user import RegisterUserRequest, UserResponse, UserProfileResponse, UpdateProfileRequest
from app.services.user_service import UserService

router = APIRouter(prefix="/api/v1/users", tags=["users"])
user_service = UserService()

@router.post("/register", response_model=UserResponse, status_code=status.HTTP_201_CREATED)
def register(request: RegisterUserRequest, db: Session = Depends(get_db)):
    try:
        user = user_service.register_user(
            db=db,
            email=str(request.email),
            first_name=request.firstName,
            middle_name=request.middleName,
            last_name=request.lastName
        )
        token = user_service.generate_jwt_token(str(user.id))
        return UserResponse(
            id=str(user.id),
            email=user.email,
            firstName=user.first_name,
            lastName=user.last_name,
            token=token
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

@router.get("/profile", response_model=UserProfileResponse, status_code=status.HTTP_200_OK)
def get_profile(
    user_id: str = Depends(get_current_user_id),
    db: Session = Depends(get_db)
):
    user = user_service.get_user_by_id(db, user_id)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="User not found"
        )
    return UserProfileResponse(
        id=str(user.id),
        email=user.email,
        firstName=user.first_name,
        lastName=user.last_name
    )

@router.put("/profile", response_model=UserProfileResponse, status_code=status.HTTP_200_OK)
def update_profile(
    request: UpdateProfileRequest,
    user_id: str = Depends(get_current_user_id),
    db: Session = Depends(get_db)
):
    if not request.firstName or request.firstName.strip() == "":
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="First name cannot be empty"
        )
    user = user_service.update_user(db, user_id, request.firstName, request.lastName)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="User not found"
        )
    return UserProfileResponse(
        id=str(user.id),
        email=user.email,
        firstName=user.first_name,
        lastName=user.last_name
    )
