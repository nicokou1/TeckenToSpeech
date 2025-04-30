import base64

from fastapi import FastAPI, HTTPException, APIRouter, Request
from fastapi.responses import RedirectResponse
from pydantic import BaseModel
from PIL import Image
from io import BytesIO
from collections import deque
from starlette.responses import JSONResponse
from datetime import datetime

app = FastAPI()
# Hela klassen är @author Nicolas K, Rawan, Hiyam. 2025-04-28
# Modell för data som skickas till /interpret
http_log = deque(maxlen=20)
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

@app.middleware("http")
async def log_requests(request: Request, call_next):
    response = await call_next(request)

    http_log.append({
        "timestamp": datetime.now().isoformat(),
        "method": request.method,
        "path": request.url.path,
        "status_code": response.status_code
    })

    return response


health_router = APIRouter(prefix="/health")

# Checkar status.
@health_router.get("/status")
def get_status():
    return {"status": "API is alive!"}

@health_router.get("/history")
def get_http_history():
    return list(http_log)

app.include_router(health_router)


@app.get("/")
def redirect_to_docs():
    return RedirectResponse(url="/docs")

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

@app.get("/fetch")
def fetch_data():
    return data_storage