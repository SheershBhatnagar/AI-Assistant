# Import all models for Alembic autogenerate
from app.db.session import Base  # noqa
from app.models.user import User  # noqa
from app.models.user_settings import UserSettings  # noqa
from app.models.otp import Otp  # noqa
from app.models.ai_model import AiModel  # noqa
from app.models.conversation import Conversation  # noqa
from app.models.message import Message, SenderType  # noqa
from app.models.system_log import SystemLog  # noqa
