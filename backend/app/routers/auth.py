from fastapi import APIRouter, status

router = APIRouter(prefix="/auth", tags=["auth"])

@router.post("/login", status_code=status.HTTP_200_OK)
async def login():
    return "Login"

@router.post("/register", status_code=status.HTTP_201_CREATED)
async def register():
    return "Register"

@router.get("/logout", status_code=status.HTTP_200_OK)
async def logout():
    return "Logout"
