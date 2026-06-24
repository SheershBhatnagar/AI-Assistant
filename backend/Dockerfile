# --- Stage 1: Build Dependencies ---
FROM python:3.11-alpine AS builder

WORKDIR /app

# Install build dependencies for C-extensions if needed
RUN apk add --no-cache gcc musl-dev postgresql-dev libffi-dev

COPY requirements.txt .
RUN pip install --no-cache-dir --user -r requirements.txt

# --- Stage 2: Final Image ---
FROM python:3.11-alpine

WORKDIR /app

# Copy python packages
COPY --from=builder /root/.local /root/.local
COPY . /app

# Ensure local bin is on path (for uvicorn, alembic, etc.)
ENV PATH=/root/.local/bin:$PATH
ENV PYTHONPATH=/app

EXPOSE 8080

# Apply migrations first, then run the uvicorn server
CMD ["sh", "-c", "alembic upgrade head && uvicorn app.main:app --host 0.0.0.0 --port 8080"]
