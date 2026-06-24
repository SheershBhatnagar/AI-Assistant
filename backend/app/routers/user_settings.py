from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.routers.deps import get_db, get_current_user_id
from app.schemas.user_settings import UpdateUserSettingsRequest, UserSettingsResponse
from app.services.user_settings_service import UserSettingsService

router = APIRouter(prefix="/api/v1/users/settings", tags=["user_settings"])
settings_service = UserSettingsService()

@router.get("", response_model=UserSettingsResponse, status_code=status.HTTP_200_OK)
def get_settings(
    user_id: str = Depends(get_current_user_id),
    db: Session = Depends(get_db)
):
    settings = settings_service.get_user_settings(db, user_id)
    if not settings:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No settings configured for user"
        )
    return UserSettingsResponse(
        id=str(settings.id),
        userId=str(settings.user_id),
        defaultModelId=str(settings.default_model_id),
        createdAt=str(settings._created_at),
        updatedAt=str(settings._updated_at)
    )

@router.put("", response_model=UserSettingsResponse, status_code=status.HTTP_200_OK)
def update_settings(
    request: UpdateUserSettingsRequest,
    user_id: str = Depends(get_current_user_id),
    db: Session = Depends(get_db)
):
    try:
        updated = settings_service.update_default_model(db, user_id, request.defaultModelId)
        return UserSettingsResponse(
            id=str(updated.id),
            userId=str(updated.user_id),
            defaultModelId=str(updated.default_model_id),
            createdAt=str(updated._created_at),
            updatedAt=str(updated._updated_at)
        )
    except ValueError as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=str(e)
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Failed to update settings: {str(e)}"
        )
