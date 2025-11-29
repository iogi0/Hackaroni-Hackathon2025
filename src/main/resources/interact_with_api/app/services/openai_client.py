from pathlib import Path
from dotenv import load_dotenv
import os
from openai import AsyncOpenAI

load_dotenv(Path(__file__).parent / ".env")

client = AsyncOpenAI(api_key=os.getenv("API_KEY") or os.getenv("OPENAI_API_KEY"))

async def ask_openai(question: str) -> str:
    response = await client.chat.completions.create(
        model="gpt-4.1",
        messages=[{"role": "user", "content": question}],
    )
    return response.choices[0].message.content
