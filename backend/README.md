# AI Assistant Backend (Python FastAPI + Alembic)

This is the Python-based backend for the AI Assistant, migrated from Kotlin Ktor.

## Features
- **FastAPI**: Asynchronous web framework for APIs.
- **SQLAlchemy (ORM)**: Database models across multiple PostgreSQL schemas (`users`, `chat`, `ai_config`, `system`).
- **Alembic**: Database migrations with multi-schema support.
- **Pytest**: Integration and unit tests for the services and database models.
- **SMTP Authentication**: Secure OTP-based authentication via email.
- **AI Integrations**: Asynchronous clients for Gemini and OpenAI.

## Local Development Setup

1. Create a Python virtual environment:
   ```bash
   python -m venv .venv
   source .venv/bin/activate  # On Windows: .venv\Scripts\activate
   ```

2. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```

3. Run migrations:
   ```bash
   alembic upgrade head
   ```

4. Start the application:
   ```bash
   uvicorn app.main:app --reload --port 8080
   ```

## Running Tests
Run pytest from the backend root:
```bash
python -m pytest
```

## Running with Docker Compose
From the project root:
```bash
docker compose up --build
```
This builds the new FastAPI image and spins up the Postgres database. Alembic migrations run automatically on container startup.
