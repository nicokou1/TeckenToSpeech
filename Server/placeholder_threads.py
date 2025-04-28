import threading
import time
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from starlette.responses import JSONResponse

# Starta FastAPI-server
app = FastAPI()

# ======= MODELLER =======
class Gesture(BaseModel):
    gesture: str
    confidence: float

class ImageInput(BaseModel):
    picture: str

class AppInput(BaseModel):
    id: int
    title: str
    body: str

# ======= API ENDPOINTS =======
@app.get("/status")
def get_status():
    return {"status": "API is alive!"}

@app.get("/app")
def send_data():
    data = [
        {
            "id": 12,
            "title": "Test123",
            "body": "TestBody123"
        }
    ]
    return JSONResponse(content=data)

@app.post("/interpret")
def interpret_gesture(data: Gesture):
    if data is None:
        raise HTTPException(status_code=404, detail=f"Item not found")
    else:
        return {
            "text": data.gesture,
            "speech_url": f"https://example.com/sounds/{data.gesture}.mp3"
        }

# ======= TRÅD FUNKTIONER =======
def lyssna_inbyggda_systemet():
    while True:
        print("[INBYGGD] Lyssnar på inbyggda systemet...")
        # Här kan du lägga kod för att ta emot data från Raspberry Pi, Arduino etc.
        time.sleep(2)

def lyssna_openCV_gester():
    while True:
        print("[OPENCV] Identifierar gester...")
        # Här kan du lägga kod för att fånga gester via OpenCV
        time.sleep(3)

# ======= STARTA TRÅDAR =======
def starta_trådar():
    inbyggd_thread = threading.Thread(target=lyssna_inbyggda_systemet)
    opencv_thread = threading.Thread(target=lyssna_openCV_gester)

    inbyggd_thread.start()
    opencv_thread.start()

# När detta körs direkt, starta trådarna och servern
if __name__ == "__main__":
    starta_trådar()
    import uvicorn
    uvicorn.run("placeholder_threads:app", host="0.0.0.0", port=8000, reload=True)
