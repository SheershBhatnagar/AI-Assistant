import logging

from fastapi import FastAPI, Request, status
from fastapi.responses import JSONResponse
from fastapi.exceptions import RequestValidationError, HTTPException
from fastapi.middleware.cors import CORSMiddleware

from app.routers import auth, user, user_settings, ai_model, chat, system_log

# Setup logger
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("main")

app = FastAPI(title="AI Assistant Backend", version="1.0.0")

# CORS middleware config
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Custom exception handler helper
def make_error_response(status_code: int, error_name: str, message: str) -> JSONResponse:
    return JSONResponse(
        status_code=status_code,
        content={
            "status": status_code,
            "error": error_name,
            "message": message
        }
    )

# Exception handlers matching Kotlin's StatusPages
@app.exception_handler(HTTPException)
async def http_exception_handler(request: Request, exc: HTTPException):
    # Retrieve detail as message
    detail = exc.detail
    if isinstance(detail, list):
        detail = str(detail)
    
    # Try to find standard HTTP status name
    error_name = "Error"
    if exc.status_code == 400:
        error_name = "Bad Request"
    elif exc.status_code == 401:
        error_name = "Unauthorized"
    elif exc.status_code == 403:
        error_name = "Forbidden"
    elif exc.status_code == 404:
        error_name = "Not Found"
    elif exc.status_code == 409:
        error_name = "Conflict"
    elif exc.status_code == 500:
        error_name = "Internal Server Error"
        
    return make_error_response(exc.status_code, error_name, detail)

@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError):
    # Format pydantic validation errors nicely
    message = "; ".join([f"{'.'.join(str(p) for p in err['loc'])}: {err['msg']}" for err in exc.errors()])
    return make_error_response(status.HTTP_400_BAD_REQUEST, "Bad Request", message)

@app.exception_handler(ValueError)
async def value_error_handler(request: Request, exc: ValueError):
    # ValueErrors map to Bad Request in Kotlin code (IllegalArgumentException -> BadRequest)
    return make_error_response(status.HTTP_400_BAD_REQUEST, "Bad Request", str(exc))

@app.exception_handler(Exception)
async def general_exception_handler(request: Request, exc: Exception):
    logger.error("Unhandled Exception caught", exc_info=exc)
    return make_error_response(
        status.HTTP_500_INTERNAL_SERVER_ERROR,
        "Internal Server Error",
        "Something went wrong on our end"
    )

# Include routers
app.include_router(auth.router)
app.include_router(user.router)
app.include_router(user_settings.router)
app.include_router(ai_model.router)
app.include_router(chat.router)
app.include_router(system_log.router)

@app.get("/")
def read_root():
    return {"status": "ok", "app": "AI Assistant Backend"}
