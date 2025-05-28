# viktiga importer...
import cv2
import imutils
import numpy as np
from tensorflow.keras.models import load_model
from constants import CLASSES
import tensorflow as tf

# cam_run.py
# @author Nicolas K, Emil F using Peer Programming (XP). 2025-05-22
# GAMMAL! klass för att starta igenkänning med vald modell.


# ROI-koordinater
TOP, RIGHT, BOTTOM, LEFT = 10, 350, 225, 590
ACCUM_WEIGHT = 0.5
CALIB_FRAMES = 30

# Global bakgrundsmodell
bg = None

# Uppdaterar bakgrundsmodellen med medelvärde.
def run_avg(gray, accumWeight=ACCUM_WEIGHT):
    global bg
    if bg is None:
        bg = gray.copy().astype("float")
    else:
        cv2.accumulateWeighted(gray, bg, accumWeight)


# Segmenterar handen i ROI och returnerar binär mask + största konturen.
def segment(roi_bgr, threshold=25, min_area=1000):

    global bg
    if bg is None:
        return None

    # HSV-mask för hudton
    hsv = cv2.cvtColor(roi_bgr, cv2.COLOR_BGR2HSV)
    lower = np.array([0, 20, 70], dtype=np.uint8)
    upper = np.array([20, 255, 255], dtype=np.uint8)
    mask_skin = cv2.inRange(hsv, lower, upper)

    # Gråskala + bakgrundsdifferens
    gray = cv2.cvtColor(roi_bgr, cv2.COLOR_BGR2GRAY)
    gray = cv2.GaussianBlur(gray, (7, 7), 0)
    diff = cv2.absdiff(bg.astype('uint8'), gray)
    _, mask_bg = cv2.threshold(diff, threshold, 255, cv2.THRESH_BINARY)

    # Kombinera masker
    mask = cv2.bitwise_and(mask_skin, mask_bg)
    kernel = np.ones((4, 4), np.uint8)
    mask = cv2.morphologyEx(mask, cv2.MORPH_CLOSE, kernel)
    mask = cv2.dilate(mask, kernel, iterations=1)
    mask = cv2.GaussianBlur(mask, (5, 5), 0)

    # Hitta största kontur
    contours, _ = cv2.findContours(mask.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    if not contours:
        return None
    largest = max(contours, key=cv2.contourArea)
    if cv2.contourArea(largest) < min_area:
        return None

    return mask, largest

# Gissar på bokstavsklass utifrån binär mask
def getPredictedClass(model, hand_roi):

    hand = cv2.resize(hand_roi, (120, 100))
    hand = hand.astype("float32") / 255.0
    hand = np.expand_dims(hand, axis=0)
    hand = np.expand_dims(hand, axis=-1)

    probs = model.predict(hand)[0]
    print("Probabilities:", probs)
    idx = np.argmax(probs)
    return CLASSES[idx]

    # Laddar in den sparade modellen
def _load_model(path="best_model_weights_continued_1.h5"):
    try:
        model = load_model(path)
        print("Modellens arkitektur och vikt: ")
        model.summary()
        return model
    except Exception as e:
        print(f"Felmeddelande: {e}")
        return None

# Huvud programmet
if __name__ == "__main__":

    model = _load_model()
    if model is None:
        exit(1)

    camera = cv2.VideoCapture(0)
    num_frames = 0

    while True:
        ret, frame = camera.read()
        if not ret:
            break

        frame = imutils.resize(frame, width=700)
        frame = cv2.flip(frame, 1)
        clone = frame.copy()

        # Definiera ROI
        roi = frame[TOP:BOTTOM, RIGHT:LEFT]
        cv2.imshow("Raw ROI", roi)
        gray = cv2.cvtColor(roi, cv2.COLOR_BGR2GRAY)
        gray = cv2.GaussianBlur(gray, (7, 7), 0)

        # Bakgrundskalibrering
        if num_frames < CALIB_FRAMES:
            run_avg(gray)
            if num_frames == 0:
                print("[STATUS] Kalibrerar bakgrund, håll handen utanför ROI...")
            elif num_frames == CALIB_FRAMES - 1:
                print("[STATUS] Bakgrund klar! Visa gest inom rutan.")
        else:
            result = segment(roi)
            if result is not None:
                mask, contour = result
                # Rita kontur
                cv2.drawContours(clone, [contour + (RIGHT, TOP)], -1, (0, 0, 255), 2)

                # Extrahera hand-ROI
                x, y, w, h = cv2.boundingRect(contour)
                hand_roi = mask[y:y+h, x:x+w]

                # Prediktion
                predicted = getPredictedClass(model, hand_roi)
                cv2.putText(clone, predicted, (70, 45),
                            cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2)

                # Visa mask
                cv2.imshow("Mask", mask)

        num_frames += 1

        # Rita ROI-ram
        cv2.rectangle(clone, (RIGHT, TOP), (LEFT, BOTTOM), (0, 255, 0), 2)
        cv2.imshow("Video Feed", clone)

        key = cv2.waitKey(1) & 0xFF
        if key == ord('q'):
            break

    camera.release()
    cv2.destroyAllWindows()