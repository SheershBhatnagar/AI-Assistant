from sqlmodel import SQLModel, Field

class Users(SQLModel, table=True):
    __tablename__ = "users"
    id: int | None = Field(default = None, primary_key = True)
    name: str | None = Field(default = None, nullable = False)
