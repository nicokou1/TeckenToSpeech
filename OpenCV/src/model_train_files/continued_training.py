# Viktiga importer...
import os
from tensorflow.keras.applications import Xception
from tensorflow.keras.layers import GlobalAveragePooling2D, Dense
from tensorflow.keras.models import Model
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.preprocessing.image import ImageDataGenerator
# continued_training.py
# @author Nicolas K, Emil F using Peer Programming (XP). 2025-05-22
# Användes för att fortsätta träna på en befintlig modell.

# Parametrar
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

# Befintliga modellen
MODEL_IN  = os.path.join(BASE_DIR, "befintligmodell.h5")

# Uppdaterade modellen
MODEL_OUT = os.path.join(BASE_DIR, "nyamodellen.h5")

# Dataset
TRAIN2 = os.path.join(BASE_DIR, "data", "SSL-dataset-1", "train")
VAL2   = os.path.join(BASE_DIR, "data", "SSL-dataset-1", "validation")

EPOCHS       = 10
BATCH_SIZE   = 32
IMAGE_HEIGHT = IMAGE_WIDTH = 224
NUM_CLASSES  = 26

# Återskapar arkitekturen med Xception
def build_model():
    base = Xception(
        weights="imagenet",
        include_top=False,
        input_shape=(IMAGE_HEIGHT, IMAGE_WIDTH, 3)
    )
    x = GlobalAveragePooling2D()(base.output)
    x = Dense(1024, activation="relu")(x)
    x = Dense(1024, activation="relu")(x)
    x = Dense(512,  activation="relu")(x)
    outputs = Dense(NUM_CLASSES, activation="softmax")(x)
    return Model(inputs=base.input, outputs=outputs)

# Ladda befintliga vikter i den nya
model = build_model()
model.load_weights(MODEL_IN)
print("Vikter tagna från: ", MODEL_IN)

# fryser tidigare lager om det behövs
for layer in model.layers[:20]:
    layer.trainable = False
for layer in model.layers[20:]:
    layer.trainable = True

# Kompilera om med låg lr för finjustering ---
opt = Adam(learning_rate=1e-5)
model.compile(optimizer=opt,
              loss="categorical_crossentropy",
              metrics=["accuracy"])

# Skapar samma datagenererings struktur som tidigare
from tensorflow.keras.applications.mobilenet import preprocess_input

train_datagen2 = ImageDataGenerator(
    preprocessing_function=preprocess_input,
    rotation_range=10,
    width_shift_range=0.1,
    height_shift_range=0.1,
    shear_range=0.1,
    zoom_range=0.1,
    fill_mode="nearest"
)
val_datagen2 = ImageDataGenerator(preprocessing_function=preprocess_input)

train_gen2 = train_datagen2.flow_from_directory(
    TRAIN2,
    target_size=(IMAGE_WIDTH, IMAGE_HEIGHT),
    batch_size=BATCH_SIZE,
    class_mode="categorical",
    shuffle=True
)
val_gen2 = val_datagen2.flow_from_directory(
    VAL2,
    target_size=(IMAGE_WIDTH, IMAGE_HEIGHT),
    batch_size=BATCH_SIZE,
    class_mode="categorical"
)

# Checkpoint för nya modellen
from tensorflow.keras.callbacks import ModelCheckpoint

checkpoint = ModelCheckpoint(
    filepath=MODEL_OUT,
    monitor="val_accuracy",
    save_best_only=True,
    save_weights_only=False,
    mode="max",
    verbose=1
)

# Fortsätt träna med dataset 2
steps_per_epoch   = train_gen2.n // BATCH_SIZE
validation_steps  = val_gen2.n // BATCH_SIZE

model.fit(
    train_gen2,
    epochs=EPOCHS,
    steps_per_epoch=steps_per_epoch,
    validation_data=val_gen2,
    validation_steps=validation_steps,
    callbacks=[checkpoint]
)

print("Färdig. Modell sparad till:", MODEL_OUT)
