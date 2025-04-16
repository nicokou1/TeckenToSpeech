import base64

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from PIL import Image
from io import BytesIO

app = FastAPI()

# Modell för data som skickas till /interpret
class Gesture(BaseModel):
    gesture: str
    confidence: float

class ImageInput(BaseModel):
    picture: str

# Test-endpoint för att kolla om servern lever
@app.get("/status")
def get_status():
    return {"status": "API is alive!"}


@app.get("/")
def try_gesture(data: Gesture):
    return {
        interpret_gesture(data)
    }

@app.post("/image")
def image_gesture(data: ImageInput):
    if data is None:
        raise HTTPException(status_code=404, detail=f"Item not found")
    else:
        image_data = base64.b64decode(data.picture)
        image = Image.open(BytesIO(image_data))
        image.show()

#test
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