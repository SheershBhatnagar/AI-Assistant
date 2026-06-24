from pydantic import BaseModel

class UpdateUserSettingsRequest(BaseModel):
    defaultModelId: str

class UserSettingsResponse(BaseModel):
    id: str
    userId: str
    defaultModelId: str
    createdAt: str
    updatedAt: str
