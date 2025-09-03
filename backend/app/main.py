from fastapi import FastAPI

from app.db.postgres import init_db
from app.routers import auth

app = FastAPI(
    title = "AI Assistant Backend",
    description="AI Assistant",
    version = "1.0.0+1",
)

@app.on_event("startup")
def on_startup():
    init_db()

@app.get("/")
async def root():
    return {"health": "ok"}

app.include_router(auth.router)
