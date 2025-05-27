# Viktiga importer...
import os
import tensorflow as tf
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from tensorflow.keras import regularizers
from tensorflow.keras.callbacks import ReduceLROnPlateau, EarlyStopping, ModelCheckpoint

# sökväg
train_dir = "data_split/train"
val_dir   = "data_split/val"

IMG_HEIGHT = 96
IMG_WIDTH  = 96
BATCH_SIZE = 32
LR         = 1e-4
EPOCHS     = 50
L2_FACTOR  = 1e-4

# skapar datagenereringen
def preprocess_input(x):
    return tf.keras.applications.mobilenet_v2.preprocess_input(x)

train_datagen = ImageDataGenerator(
    preprocessing_function=preprocess_input,
    rotation_range=25,
    width_shift_range=0.25,
    height_shift_range=0.25,
    shear_range=15,
    zoom_range=0.25,
    horizontal_flip=True,
    brightness_range=(0.6, 1.4)
)

val_datagen = ImageDataGenerator(preprocessing_function=preprocess_input)

train_gen = train_datagen.flow_from_directory(
    train_dir,
    target_size=(IMG_HEIGHT, IMG_WIDTH),
    batch_size=BATCH_SIZE,
    class_mode='categorical',
    shuffle=True
)

val_gen = val_datagen.flow_from_directory(
    val_dir,
    target_size=(IMG_HEIGHT, IMG_WIDTH),
    batch_size=BATCH_SIZE,
    class_mode='categorical',
    shuffle=False
)

# Bygger basmodellen
base_model = tf.keras.applications.MobileNetV2(
    input_shape=(IMG_HEIGHT, IMG_WIDTH, 3),
    include_top=False,
    weights='imagenet'
)
base_model.trainable = False

x = tf.keras.layers.GlobalAveragePooling2D()(base_model.output)
x = tf.keras.layers.Dropout(0.4)(x)
outputs = tf.keras.layers.Dense(
    train_gen.num_classes,
    activation='softmax',
    kernel_regularizer=regularizers.l2(L2_FACTOR)
)(x)

model = tf.keras.Model(inputs=base_model.input, outputs=outputs, name="hand_gesture_ft")
model.compile(
    optimizer=tf.keras.optimizers.Adam(learning_rate=LR),
    loss='categorical_crossentropy',
    metrics=['accuracy']
)

model.summary()

# Callbacks för att visa hur checkpointen gick
callbacks = [
    ReduceLROnPlateau(
        monitor='val_loss',
        factor=0.5,
        patience=3,
        verbose=1
    ),
    EarlyStopping(
        monitor='val_loss',
        patience=8,
        restore_best_weights=True,
        verbose=1
    ),
    ModelCheckpoint(
        filepath='best_model.keras',
        monitor='val_accuracy',
        save_best_only=True,
        verbose=1
    )
]

# Tränar modellen enligt antal epoker, validerings data och callback info
history = model.fit(
    train_gen,
    epochs=EPOCHS,
    validation_data=val_gen,
    callbacks=callbacks
)

# Sparar modellen.
model.save('hand_gesture_model.keras')
print("Modell sparad")