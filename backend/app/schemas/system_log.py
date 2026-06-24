from pydantic import BaseModel

class CreateLogRequest(BaseModel):
    userId: str
    action: str
    details: str

class SystemLogResponse(BaseModel):
    id: str
    action: str
    details: str
    createdAt: str
