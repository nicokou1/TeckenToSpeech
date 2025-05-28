# viktiga importer..
import os
import cv2
import requests
import numpy as np
from PIL import Image
import picamera2 as cam
from tensorflow.keras.applications import Xception
from tensorflow.keras.layers import GlobalAveragePooling2D, Dense
from tensorflow.keras.models import Model
from tensorflow.keras.applications.mobilenet import preprocess_input

# start_predict.py
# @author Nicolas K, Emil F using Peer Programming (XP). 2025-05-22
# Klassen som startar video, påbörjar prediktering samt
# skickar över data till server.

# Konstanter som måste matcha övriga filer.
IMAGE_HEIGHT = 224
IMAGE_WIDTH  = 224
DATASET_CATEGORIES = 26

WEIGHTS_PATH = os.path.join(os.path.dirname(__file__),
                            "super_duper_best_weights.h5")
SERVER_URL = "http://51.21.255.36:8000/letter"

# återskapar arkitekturen enligt Xception
def build_model_architecture():
    base = Xception(
        weights='imagenet',
        include_top=False,
        input_shape=(IMAGE_HEIGHT, IMAGE_WIDTH, 3)
    )
    x = GlobalAveragePooling2D()(base.output)
    x = Dense(1024, activation='relu')(x)
    x = Dense(1024, activation='relu')(x)
    x = Dense(512, activation='relu')(x)
    outputs = Dense(DATASET_CATEGORIES, activation='softmax')(x)
    return Model(inputs=base.input, outputs=outputs)

# Laddar enbart vikter och kompilerar dem
def load_model_weights_only(weights_path):
    model = build_model_architecture()
    model.load_weights(weights_path)
    model.compile(
        optimizer='Adam',
        loss='categorical_crossentropy',
        metrics=['accuracy']
    )
    return model

# Förbehandlar openCV-frames för predict() metoden
def preprocess_frame(frame):
    frame_resized = cv2.resize(frame, (IMAGE_WIDTH, IMAGE_HEIGHT))
    arr = np.expand_dims(frame_resized, axis=0)
    return preprocess_input(arr)

# Hämtar de tre högsta gissade bokstäverna
def get_top_predictions(preds):
    letters = list("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
    probs = preds.flatten()
    pairs = sorted(zip(letters, probs),
                   key=lambda x: x[1], reverse=True)
    return pairs[:3], pairs

# Skapar själva strömmen från kameran och börjar gissa
def video_stream(model):
    cap = cam.Picamera2()
    config = cap.create_preview_configuration({"format": "RBG888", "size": (IMAGE_WIDTH, IMAGE_HEIGHT)})
    cap.configure(config)
    cap.start()
    #cap = cv2.VideoCapture(0)
    word = []
    #cv2.namedWindow("SignInterpreterSSL", cv2.WINDOW_AUTOSIZE)
    while True:
        #ret, frame = cap.capture_array()
        ret, frame = cap.read()
        if not ret:
            break

        # Förbehandla och gissa
        inp = preprocess_frame(frame)
        preds = model.predict(inp)
        top3, _ = get_top_predictions(preds)
        letter, conf = top3[0]  # Mest sannolika bokstaven

        # Visa gissning, text och textruta på skärmen

        cv2.putText(frame,
                    f"{letter} ({conf*100:.1f}%)",
                    (10, 30), cv2.FONT_HERSHEY_SIMPLEX,
                    1.0, (0,255,0), 2)
        cv2.putText(frame,
                    "Tryck på S för att spara bokstav, Q för att avsluta",
                    (10, IMAGE_HEIGHT-10),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255,255,255), 1)
        cv2.putText(frame,
                    "Ord: " + "".join(word),
                    (10, IMAGE_HEIGHT-40),
                    cv2.FONT_HERSHEY_SIMPLEX, 1.0, (0,200,200), 2)

        cv2.imshow("SignInterpreterSSL", frame)
        key = cv2.waitKey(1) & 0xFF

        if key == ord('s'):
            # Tryck S för att fånga just denna bokstav
            word.append(letter)
            print("Captured:", letter)
        elif key == ord('d') and word:
            # D för att ta bort senaste bokstav
            word.pop()
            print("Deleted last letter")
        elif key == ord('r'):
            # R för ta bort alla sparade bokstäver
            word.clear()
            print("Reset word")
        elif key in (ord('q'), 27):
            # Q eller Esc för att avsluta programmet
            break

        elif key == ord('g'):
            # G för att skicka hela ordet bokstav för bokstav till servern
            if word:
                text_to_send = "".join(word)
                print("Skickar ord till server:", text_to_send)
                for letter in text_to_send:
                    try:
                        response = requests.post(
                            SERVER_URL,
                            json={"letter": letter}
                        )
                        if response.status_code == 200:
                            print(f"Skickade bokstav '{letter}'.")
                            word.clear()

                        else:
                            print(f"Misslyckades att skicka '{letter}'. Serverns svar: {response.status_code}")
                    except Exception as e:
                        print(f"Misslyckades att skicka '{letter}' till servern:", e.args)
            else:
                print("Inget ord har upptäckts.")

    cap.release()
    cv2.destroyAllWindows()


# Start metoden
def main():
    model = load_model_weights_only(WEIGHTS_PATH)
    video_stream(model)


if __name__ == "__main__":
    main()
