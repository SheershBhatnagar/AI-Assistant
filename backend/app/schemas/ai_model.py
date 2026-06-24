from pydantic import BaseModel

class CreateAiModelRequest(BaseModel):
    userId: str
    name: str
    apiKey: str

class AiModelResponse(BaseModel):
    id: str
    name: str
    createdAt: str
