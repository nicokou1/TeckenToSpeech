import threading
import json
import time
import requests
from io import BytesIO
import queue

fps = 10 # the fps of the video sent to the server.
urlmain = "http://51.21.222.0:8000"
connected = True
buffer = queue.Queue()

def checkConnection():
    global urlmain
    url = urlmain+"/status"
    try:
        response = requests.get(url, timeout=1)
        print(response.json())
        return response.status_code == 200
    except:
        return False

# Server method
def server_method():
    global fps
    global urlmain
    global connected
    
    url = urlmain+"/upload"
    while True:
        try:
            firstjson = buffer.get()
            print(requests.post(url, json=firstjson, timeout=1).json()) 
        except Exception as e:
            print(e)
        
def send(gesture, confidence):
    global buffer
    data = {
        "gesture":gesture,
        "confidence":confidence
    }
    
    buffer.put(data)
    
    # TODO: ADD THE BUFFER OF IMAGES TO SEND.
    return
    
def get():
    global urlmain
    url = urlmain+"/fetchData"
    try:
        response = requests.get(url, timeout=1)
        print(response.json())
    except:
        print("horunge")
    return
    