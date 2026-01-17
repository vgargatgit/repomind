import os
from typing import List

from fastapi import FastAPI
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer

MODEL_NAME = os.getenv(
    "EMBEDDING_MODEL_NAME",
    "sentence-transformers/code-bert-tiny-code-search",
)

app = FastAPI()
model = SentenceTransformer(MODEL_NAME)


class EmbedRequest(BaseModel):
    inputs: List[str]


class EmbedResponse(BaseModel):
    embeddings: List[List[float]]


@app.get("/health")
def health() -> dict:
    return {"status": "ok", "model": MODEL_NAME}


@app.post("/embed", response_model=EmbedResponse)
def embed(request: EmbedRequest) -> EmbedResponse:
    if not request.inputs:
        return EmbedResponse(embeddings=[])
    embeddings = model.encode(request.inputs, normalize_embeddings=True, show_progress_bar=False)
    return EmbedResponse(embeddings=embeddings.tolist())
