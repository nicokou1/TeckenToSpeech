import requests
import time
def detect_gesture():
    #OpenCV logik
    return "C", 0.93


while True:
    gesture, confidence = detect_gesture()

    if gesture is not None and confidence >= 0.85:
        data = {
            "gesture":gesture,
            "confidence":confidence
        }

        url = "http://127.0.0.1:8000/interpret"
        try:
            response = requests.post(url, json=data)
            print("Svar från server:", response.json())
        except Exception as e:
            print("Misslyckades att skicka data:", e)

        time.sleep(0.5)

        url = "http://127.0.0.1:8000/status"
        try:
            response = requests.get(url)
            print("Server status: ", response.text)
        except Exception as e:
            print("Ingen status.")

        time.sleep(0.5)
