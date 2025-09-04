from sqlmodel import SQLModel, create_engine, Session, select

from app.models.users import Users

DATABASE_URL = "postgresql+psycopg2://root:password@127.0.0.1:5432/ai_assistant"

engine = create_engine(DATABASE_URL, echo=True)

def init_db():
    SQLModel.metadata.create_all(engine)

def get_session():
    with Session(engine) as session:
        yield session
