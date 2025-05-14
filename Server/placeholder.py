import base64
import os
import time

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
#UPLOAD_FOLDER = "saved_images"

#os.makedirs(UPLOAD_FOLDER, exist_ok=False)

http_log = deque(maxlen=20)
data_storage = deque(maxlen=2)
letter_queue = deque()

class LetterInput(BaseModel):
    letter: str

# Modell för data som ska hämtas med /image

# Modell för data som ska skickas till /app
class AppInput(BaseModel):
    id: int
    title: str
    body: str

# Metod som fångar upp HTTP metodanrops meddelanden
# Som sedan lagras i en lista som i sin höjd har de
# 20 senaste anropen.
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

# Checkar status på Servern.
# Om Servern är aktiv skickas vårt returmeddelande
# Annars felkod.
@health_router.get("/status")
def get_status():
    return {"status": "API is alive!"}

@health_router.get("/history")
def get_http_history():
    return list(http_log)

app.include_router(health_router)

# Metod som skickar användaren till dokumentations sidan.
@app.get("/")
def redirect_to_docs():
    return RedirectResponse(url="/docs")

# Metod där Klienten hämtar fejk data i form av JSON.
# Vid riktig implementation skall fejk datan vara den data
# vi får från det Inbyggda systemet.
@app.get("/app")
def send_data():
    data = {
        "letter": "A"
    }
    return JSONResponse(content=data)

@app.post("/letter")
def enqueue_letter(data: LetterInput):  
    if not data.letter:
        raise HTTPException(status_code=400, detail="No letter provided")

    letter_queue.append(data.letter)
    return {"response": "Letter queued"}

@app.get("/letter")
def dequeue_letter():
    if not letter_queue:
        raise HTTPException(status_code=404, detail="No letters in queue")
    next_letter = letter_queue.popleft()
    return JSONResponse(content={"letter": next_letter})


@app.get("/queue")
def peek_queue():
    # return the list of pending letters without removing
    return {"pending_letters": list(letter_queue)}
