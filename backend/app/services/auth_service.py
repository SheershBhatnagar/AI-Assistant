import random
from datetime import datetime, timedelta
from sqlalchemy.orm import Session

from app.models.user import User
from app.models.otp import Otp
from app.utils.email import send_email
from app.utils.jwt_helper import generate_jwt_token

class AuthService:
    def send_otp(self, db: Session, email: str) -> bool:
        # 1. Find user or create a new one
        user = db.query(User).filter(User.email == email).first()
        if not user:
            user = User(
                email=email,
                first_name="User",
                middle_name=None,
                last_name=None
            )
            db.add(user)
            db.commit()
            db.refresh(user)

        # 2. Generate a 6-digit OTP
        otp_code = str(random.randint(100000, 999999))

        # 3. Save OTP (Expires in 10 minutes)
        expires_at = datetime.utcnow() + timedelta(minutes=10)
        new_otp = Otp(
            user_id=user.id,
            otp=otp_code,
            expires_at=expires_at,
            is_used=False
        )
        db.add(new_otp)
        db.commit()

        # 4. Construct email
        email_body = f"""
            <div style="font-family: Arial, sans-serif; text-align: center; padding: 20px;">
                <h2>Welcome to AI Assistant</h2>
                <p>Your secure One-Time Password (OTP) to log in is:</p>
                <h1 style="color: #4A90E2; letter-spacing: 5px;">{otp_code}</h1>
                <p style="color: #888;">This code will expire in 10 minutes. Do not share it with anyone.</p>
            </div>
        """

        # 5. Send it
        is_sent = send_email(
            to_address=email,
            subject=f"Your Login Code: {otp_code}",
            html_body=email_body.strip()
        )

        if not is_sent:
            raise RuntimeError("Failed to send the OTP email. Please try again later.")

        return True

    def verify_otp_and_login(self, db: Session, email: str, otp_code: str) -> str:
        user = db.query(User).filter(User.email == email).first()
        if not user:
            raise ValueError("User not found")

        # Get valid OTP
        otp_record = db.query(Otp).filter(
            Otp.user_id == user.id,
            Otp.otp == otp_code,
            Otp.is_used == False
        ).order_by(Otp._created_at.desc()).first()

        if not otp_record:
            raise ValueError("Invalid OTP")

        if otp_record.expires_at < datetime.utcnow():
            raise ValueError("OTP has expired")

        # Mark OTP as used
        otp_record.is_used = True
        db.commit()

        # Issue JWT
        return generate_jwt_token(str(user.id))
