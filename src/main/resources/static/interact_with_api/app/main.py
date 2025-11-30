from fastapi import FastAPI
from pydantic import BaseModel
from .services.openai_client import (
    ask_give_materials,
    ask_make_start_test,
    ask_openai,
    ask_field_guide,
    ask_make_test,
    detect_intent_and_slots,
)

app = FastAPI()

class FieldGuideQuery(BaseModel):
    field: str
    age: str

class StartTestQuery(BaseModel):
    theme: str
    guide: str
    age: str

class MakeTestQuery(BaseModel):
    field: str
    reviewed_topics: str
    guide: str
    age: str

class MaterialsQuery(BaseModel):
    field: str
    theme: str
    guide: str
    age: str
    test_results: str

class QuestionQuery(BaseModel):
    field: str
    question: str
    guide: str
    current_topics: str
    age: str

@app.post("/api/ask/field-guide")
async def field_guide(payload: FieldGuideQuery):
    guide = await ask_field_guide(payload.field, payload.age)
    return {"answer": guide}

@app.post("/api/ask/make_start_test")
async def make_start_test(payload: StartTestQuery):
    start_test = await ask_make_start_test(payload.theme, payload.guide, payload.age)
    return {"answer": start_test}

@app.post("/api/ask/make_test")
async def make_test(payload: MakeTestQuery):
    test = await ask_make_test(payload.field, payload.reviewed_topics, payload.guide, payload.age)
    return {"answer": test}

@app.post("/api/ask/materials")
async def ask_materials(payload: MaterialsQuery):
    materials = await ask_give_materials(payload.field, payload.theme, payload.guide, payload.age, payload.test_results)
    return {"answer": materials}

@app.post("/api/ask")
async def ask_llm(payload: QuestionQuery):
    response = await ask_openai(payload.field, payload.question, payload.guide, payload.current_topics, payload.age)
    return {"answer": response}


class SmartQuery(BaseModel):
    field: str
    guide: str
    age: str
    message: str


@app.post("/api/ask/smart")
async def ask_smart(payload: SmartQuery):
    intent_data = await detect_intent_and_slots(payload.message)
    intent = intent_data["intent"]
    slots = intent_data["slots"]

    slots.setdefault("field", payload.field)
    slots.setdefault("guide", payload.guide)
    slots.setdefault("age", payload.age)

    if intent == "MakeTestQuery":
        result = await ask_make_test(
            slots["field"],
            slots["reviewed_topics"],
            slots["guide"],
            slots["age"]
        )
        return {"intent": intent, "answer": result}

    if intent == "MaterialsQuery":
        result = await ask_give_materials(
            slots["field"],
            slots["theme"],
            slots["guide"],
            slots["age"],
            slots["test_results"]
        )
        return {"intent": intent, "answer": result}

    if intent == "QuestionQuery":
        result = await ask_openai(
            slots["field"],
            slots["question"],
            slots["guide"],
            slots["current_topics"],
            slots["age"]
        )
        return {"intent": intent, "answer": result}

    return {"error": "unknown intent", "debug": intent_data}