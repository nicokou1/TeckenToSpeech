import string
from tensorflow.keras.applications import Xception
from tensorflow.keras.layers import GlobalAveragePooling2D, Dense
from tensorflow.keras.models import Model
import os

# adjust_model_by_hand.py
# @author Nicolas K, Emil F using Peer Programming (XP). 2025-05-22
# Tar in befintlig modell och justerar enstaka vikter för hand.

# Konstanter som ska matcha träningskonstanterna
IMAGE_HEIGHT = 224
IMAGE_WIDTH  = 224
NUM_CLASSES  = 26

# Tar en modell vars vikter skall justeras
WEIGHTS_IN  = os.path.join(os.path.dirname(__file__),
                           "exempel.h5")
WEIGHTS_OUT = os.path.join(os.path.dirname(__file__),
                           "exempel_med_höjd_vikt.h5")

# Återskapar arkitekturen enligt Xception
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
    outputs = Dense(NUM_CLASSES, activation='softmax')(x)
    return Model(inputs=base.input, outputs=outputs)

# Laddar in vikterna från vald modell
model = build_model_architecture()
model.load_weights(WEIGHTS_IN)

# Hämtar det sista lagrets vikter & bias (vad den tror att saker är)
dense = model.layers[-1]
W, b = dense.get_weights()

# Kan justeras för att höja eller sänka vikten hos en specifik bokstav
letters = list(string.ascii_uppercase)
delta = 1.0
for i, letter in enumerate(letters):
    if letter in ('X'):
        b[i] -= delta
        print(f"Vikt för {letter} ökades med {delta}")

# Lägger in nya vikter och sparar i en ny modell fil.
dense.set_weights([W, b])
model.save_weights(WEIGHTS_OUT)
print("Sparade vikter i ny fil: ", WEIGHTS_OUT)
