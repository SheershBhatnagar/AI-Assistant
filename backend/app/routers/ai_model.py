from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List

from app.routers.deps import get_db, get_current_user_id
from app.schemas.ai_model import CreateAiModelRequest, AiModelResponse
from app.services.ai_model_service import AiModelService

router = APIRouter(tags=["models"])
ai_model_service = AiModelService()

@router.post("/api/v1/models", response_model=AiModelResponse, status_code=status.HTTP_201_CREATED)
def add_model(
    request: CreateAiModelRequest,
    user_id: str = Depends(get_current_user_id),
    db: Session = Depends(get_db)
):
    try:
        created = ai_model_service.add_model_config(
            db=db,
            user_id=user_id, # Use secure JWT user ID
            name=request.name,
            api_key=request.apiKey
        )
        return AiModelResponse(
            id=str(created.id),
            name=created.name,
            createdAt=str(created._created_at)
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

@router.get("/api/v1/models", response_model=List[AiModelResponse], status_code=status.HTTP_200_OK)
def get_models(
    user_id: str = Depends(get_current_user_id),
    db: Session = Depends(get_db)
):
    try:
        models = ai_model_service.get_user_models(db, user_id)
        return [
            AiModelResponse(
                id=str(m.id),
                name=m.name,
                createdAt=str(m._created_at)
            ) for m in models
        ]
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=str(e)
        )

@router.get("/api/v1/users/{path_user_id}/models", response_model=List[AiModelResponse], status_code=status.HTTP_200_OK)
def get_user_models(
    path_user_id: str,
    user_id: str = Depends(get_current_user_id),
    db: Session = Depends(get_db)
):
    if path_user_id != user_id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Access to other user configurations is denied"
        )
    try:
        models = ai_model_service.get_user_models(db, user_id)
        return [
            AiModelResponse(
                id=str(m.id),
                name=m.name,
                createdAt=str(m._created_at)
            ) for m in models
        ]
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=str(e)
        )
