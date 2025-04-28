import base64

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from PIL import Image
from io import BytesIO

from starlette.responses import JSONResponse

app = FastAPI()
# Hela klassen är @author Nicolas K, Rawan, Hiyam. 2025-04-28
# Modell för data som skickas till /interpret

data_storage = []
class Gesture(BaseModel):
    gesture: str
    confidence: float
# Modell för data som ska hämtas med /image
class ImageInput(BaseModel):
    picture: str
# Modell för data som ska skickas till /app
class AppInput(BaseModel):
    id: int
    title: str
    body: str

# Test-endpoint för att kolla om servern lever
@app.get("/status")
def get_status():
    return {"status": "API is alive!"}

def send_datatest():
    data = [
        {
            "gesture":"A",
            "confidence": 0.93
        }
    ]
    return JSONResponse(content=data)
@app.get("/app")
def send_data():
    data = [
    {
        "id":12,
        "title":"Mimoza",
        "body":"ärcool123"
    }
    ]
    return JSONResponse(content=data)
@app.get("/")
def try_gesture(data: Gesture):
    return {
        interpret_gesture(data)
    }
@app.post("/test")
def test_image():
    return send_datatest()

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

@app.post("/upload")
def upload_gesture(data: Gesture):
    if data is None:
        raise HTTPException(status_code=404, detail=f"Trash data :(")
    else:
        data_storage.append(data.model_dump())
        return {
            "response": "OK!"
        }

@app.get("/fetchData")
def fetch_data():
    return data_storage