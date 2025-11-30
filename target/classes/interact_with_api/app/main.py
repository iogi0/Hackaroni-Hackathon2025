from fastapi import FastAPI
from pydantic import BaseModel
from services.openai_client import ask_openai

app = FastAPI()

class Query(BaseModel):
    question: str

@app.post("/api/ask")
async def ask_llm(payload: Query):
    response = await ask_openai(payload.question)
    return {"answer": response}