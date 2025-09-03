from sqlmodel import SQLModel, create_engine, Session, select

from app.models.users import Users

DATABASE_URL = "postgresql+psycopg2://root:password@127.0.0.1:5433/busybee"

engine = create_engine(DATABASE_URL, echo=True)

def init_db():
    SQLModel.metadata.create_all(engine)

def get_session():
    with Session(engine) as session:
        yield session

with Session(engine) as session:
    test1 = Users(name="test1")
    test2 = Users(name="test2")
    session.add(test1)
    session.add(test2)
    session.commit()

with Session(engine) as session:
    statement = select(Users)
    results = session.exec(statement)
    for user in results:
        print(user.name)
