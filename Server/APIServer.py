# Nödvändiga importer..
from fastapi import FastAPI, HTTPException, APIRouter, Request
from fastapi.responses import RedirectResponse
from pydantic import BaseModel
from collections import deque
from starlette.responses import JSONResponse
from datetime import datetime

# @author Nicolas K, Rawan, Hiyam. 2025-05-22
# Detta är koden för API servern där all HTTP kommunikation mellan våra enheter finns.


app = FastAPI()

# Köer
http_log = deque(maxlen=20)
letter_queue = deque()

# Prefix
health_router = APIRouter(prefix="/health")


# Letter mall för bokstäver som skickas mellan IS & Klient.
# Innehåller en sträng med bokstav.
# @author Hiyam
class LetterInput(BaseModel):
    letter: str

# Metod som fångar upp HTTP metodanrops meddelanden.
# Lagras sedan i en lista som i sin höjd har de 20 senaste anropen.
# @author Nicolas K
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


# GET status som hämtar aktuell status på servern.
# Ifall server är aktiv, returmeddelande med aktuell status,
# annars felkod.
# @author Rawan
@health_router.get("/status")
def get_status():
    return {"status": "API is alive!"}


# GET history metod som hämtar historiken av HTTP anrop på server sedan start.
# @author Nicolas K
@health_router.get("/history")
def get_http_history():
    return list(http_log)

app.include_router(health_router)

# GET metod som skickar användaren till den interaktiva docs sidan.
# @author Nicolas K
@app.get("/")
def redirect_to_docs():
    return RedirectResponse(url="/docs")


# POST letter metod som det inbyggda systemet använder för att
# skicka den tolkade bokstaven till servern.
# Lagrar sedan bokstaven i en kö enligt LIFO.
# @author Nicolas K
@app.post("/letter")
def enqueue_letter(data: LetterInput):  
    if not data.letter:
        raise HTTPException(status_code=400, detail="No letter provided")

    letter_queue.append(data.letter)
    return {"response": "Letter queued"}


# GET letter metod som app-klienten använder för att
# hämta tolkade bokstäver från servern.
# Den hämtade bokstaven tas sedan bort från kön.
# @author Nicolas K
@app.get("/letter")
def dequeue_letter():
    if not letter_queue:
        raise HTTPException(status_code=404, detail="No letters in queue")
    next_letter = letter_queue.popleft()
    return JSONResponse(content={"letter": next_letter})


# GET queue metod som hämtar listan på aktuell kö.
# Användningsfall är genom den interaktiva dokumentationen, FastAPI.
# @author Hiyam
@app.get("/queue")
def peek_queue():
    return {"pending_letters": list(letter_queue)}
