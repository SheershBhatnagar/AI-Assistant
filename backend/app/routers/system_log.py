from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List

from app.routers.deps import get_db
from app.schemas.system_log import CreateLogRequest, SystemLogResponse
from app.services.system_log_service import SystemLogService

router = APIRouter(tags=["logs"])
log_service = SystemLogService()

@router.post("/api/v1/logs", response_model=SystemLogResponse, status_code=status.HTTP_201_CREATED)
def create_log(request: CreateLogRequest, db: Session = Depends(get_db)):
    try:
        log = log_service.log_action(
            db=db,
            user_id=request.userId,
            action=request.action,
            details=request.details
        )
        return SystemLogResponse(
            id=str(log.id),
            action=log.action,
            details=log.details,
            createdAt=str(log._created_at)
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=str(e)
        )

@router.get("/api/v1/users/{user_id}/logs", response_model=List[SystemLogResponse], status_code=status.HTTP_200_OK)
def get_user_logs(user_id: str, db: Session = Depends(get_db)):
    try:
        logs = log_service.get_user_logs(db, user_id)
        return [
            SystemLogResponse(
                id=str(l.id),
                action=l.action,
                details=l.details,
                createdAt=str(l._created_at)
            ) for l in logs
        ]
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=str(e)
        )
