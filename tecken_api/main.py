from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

app = FastAPI()
# hej test
# Modell för data som skickas till /interpret
class Gesture(BaseModel):
    gesture: str
    confidence: float

# Test-endpoint för att kolla om servern lever
@app.get("/status")
def get_status():
    return {"status": "API is alive!"}


@app.get("/")
def try_gesture(data: Gesture):
    return {
        interpret_gesture(data)
    }

# API för att tolka gest
@app.post("/interpret")
def interpret_gesture(data: Gesture):
    if data is None:
        raise HTTPException(status_code=404, detail=f"Item not found")
    else:
        return {
            "text": data.gesture,
            "speech_url": f"https://example.com/sounds/{data.gesture}.mp3"
        }