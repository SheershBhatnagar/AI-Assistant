import os
import time
from typing import Optional
from jose import jwt, JWTError

JWT_SECRET = os.getenv("JWT_SECRET_TOKEN", "this-is-a-super-secret-jwt-token")
AUDIENCE = "ai-assistant-users"
ISSUER = "http://localhost:8080/"

def generate_jwt_token(user_id: str) -> str:
    # 24 hours in seconds
    expire_timestamp = int(time.time()) + 86400
    payload = {
        "iss": ISSUER,
        "aud": AUDIENCE,
        "userId": user_id,
        "exp": expire_timestamp
    }
    return jwt.encode(payload, JWT_SECRET, algorithm="HS256")

def decode_jwt_token(token: str) -> Optional[dict]:
    try:
        # Note: In standard python-jose, we decode passing the options/aud/iss
        payload = jwt.decode(
            token,
            JWT_SECRET,
            algorithms=["HS256"],
            audience=AUDIENCE,
            issuer=ISSUER
        )
        return payload
    except JWTError as e:
        print(f"JWT decode error: {e}")
        return None
