from dotenv import load_dotenv
from pathlib import Path
import os
import json
from openai import AsyncOpenAI

load_dotenv(Path(__file__).parent / ".env")
client = AsyncOpenAI(api_key=os.getenv("API_KEY") or os.getenv("OPENAI_API_KEY"))

async def cheap_llm(prompt: str) -> str:
    """Use a cheaper model to classify intent and extract slots. Returns raw JSON text."""
    response = await client.chat.completions.create(
        model="gpt-4o-mini",
        messages=[
            {"role": "system", "content": "You are a JSON-only classifier. Respond with valid JSON and nothing else. and adapt to any language"},
            {"role": "user", "content": prompt}
        ]
    )
    return response.choices[0].message.content

async def detect_intent_and_slots(message: str):
    """Detect the user's intent and extract slots from a free-form message."""
    prompt = f"""
You are a classifier. Detect user intent and extract all possible fields.

Available intents:
- "MakeTestQuery"
- "MaterialsQuery"
- "QuestionQuery"

Return valid JSON:
{{
  "intent": "...",
  "slots": {{
     "...": "..."
  }}
}}
User message: "{message}"
"""

    response = await cheap_llm(prompt)
    return json.loads(response)

async def ask_field_guide(field: str, age: str) -> str:
    """Create an age-appropriate learning plan for a given field."""
    response = await client.chat.completions.create(
        model="gpt-4.1",
        messages=[
            {
                "role": "system",
                "content": "You are an experienced teacher who knows how to find an approach to people of any age and any level of education. and adapt to any language"
            },
            {
                "role": "user",
                "content": f"Please create the best possible learning plan for a person aged {age} years in the field of {field}. The learning should not be too boring or too difficult. At the end, give me a personalised list of actions that best suits the learning algorithm in this field {field}. without emojis."
            }
        ]
    )
    return response.choices[0].message.content


async def ask_make_start_test(theme: str, guide: str, age: str) -> str:
    """Create a short placement test tailored to theme, guide, and age."""
    response = await client.chat.completions.create(
        model="gpt-4.1",
        messages=[
            {
                "role": "system",
                "content": "You are an experienced teacher in the theme specified by the user and can adapt tasks to any learner. and adapt to any language"
            },
            {
                "role": "user",
                "content": f"Please create a short test for me that covers as many topics as possible to check the knowledge of a person aged {age} years on the topic {theme} and using this teaching method {guide}. Provide a personalised short test (maximum 5 examples or 10 questions on the topic) for a person of this age. without emojis."
            }
        ]
    )
    return response.choices[0].message.content


async def ask_make_test(field: str, reviewed_topics: str, guide: str, age: str) -> str:
    """Create a targeted practice test based on recently reviewed topics."""
    response = await client.chat.completions.create(
        model="gpt-4.1",
        messages=[
            {
                "role": "system",
                "content": "You are an experienced teacher who crafts targeted practice based on recent study logs. and adapt to any language"
            },
            {
                "role": "user",
                "content": f"Create a test for me in the field of {field} based on the topics recently reviewed: {reviewed_topics}. Use the guide {guide}. Provide up to 5 examples or up to 10 questions personalised for a {age}-year-old learner."
            }
        ]
    )
    return response.choices[0].message.content


async def ask_give_materials(field: str, theme: str, guide: str, age: str, test_results: str) -> str:
    """Analyse test results, identify gaps, and provide a remediation plan with materials."""
    response = await client.chat.completions.create(
        model="gpt-4.1",
        messages=[
            {
                "role": "system",
                "content": "You are an experienced teacher who can analyse test results and build actionable remediation plans. and adapt to any language"
            },
            {
                "role": "user",
                "content": f"Check these test results for the {theme} theme in the field of {field}: {test_results}. Identify mistakes, map them to topics, create a study plan using the {guide} method for a {age}-year-old, and include deadlines plus a highly rated YouTube video link. without emojis."
            }
        ]
    )
    return response.choices[0].message.content


async def ask_openai(field: str, question: str, guide: str, current_topics: str, age: str) -> str:
    """Answer a question with personalised explanation using guide and current topics."""
    response = await client.chat.completions.create(
        model="gpt-4.1",
        messages=[
            {
                "role": "system",
                "content": "You are an experienced teacher who tailors explanations to the learner's field, age, and study topics. and adapt to any language"
            },
            {
                "role": "user",
                "content": f"Answer the question in the field of {field}: {question}. Use the guide {guide}, reference the current topics {current_topics}, and personalise the explanation for a {age}-year-old. without emojis."
            }
        ]
    )
    return response.choices[0].message.content
