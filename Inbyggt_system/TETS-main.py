import cv2
import numpy as np
from libcamera import Transform
from picamera2 import Picamera2
from Xlib import display

# Get the screen resolution

d = display.Display()

screen = d.screen()

screen_width = screen.width_in_pixels
screen_height = screen.height_in_pixels

print(f"Screen Resolution: {screen_width}x{screen_height}")

picam2 = Picamera2()

# Get the camera resolution

camera_resolution = picam2.preview_configuration.main.size
camera_width = camera_resolution[0]
camera_height = camera_resolution[1]

print(f"Camera Resolution: {camera_width}x{camera_height}")

# Calculate the camera resolution based on screen size

camera_multiplier = screen_width / camera_width

calculated_width = int(camera_width * camera_multiplier)
calculated_height = int(camera_height * camera_multiplier)

print(f"Camera Calcuated Resolution: {calculated_width}x{calculated_height}")

config = picam2.create_preview_configuration(
    {"format": "RGB888", "size": (calculated_width, calculated_height)},
    transform=Transform(hflip=True),
)

picam2.configure(config)
picam2.start()

window_name = "TeTS Fullscreen"
cv2.namedWindow(window_name, cv2.WND_PROP_FULLSCREEN)
cv2.setWindowProperty(window_name, cv2.WND_PROP_FULLSCREEN, cv2.WINDOW_FULLSCREEN)

while True:
    # Capture Frame
    frame = picam2.capture_array()
    height, width = frame.shape[:2]
    y1 = (height - screen_height) // 2
    y2 = y1 + screen_height

    # Crop the frame
    cropped = frame[y1:y2, :]

    text = "TeTS Feed"

    # Get the size of the text to be drawn
    text_size = cv2.getTextSize(text, cv2.FONT_HERSHEY_SIMPLEX, 1.2, 2)[0]
    text_width, text_height = text_size

    # Define text position (ensure it's inside the cropped area)
    text_x, text_y = 10, 40  # Adjust Y position to be inside the cropped area

    text_x = (cropped.shape[1] - text_width) // 2  # Center horizontally

    # Add text to the cropped frame
    cv2.putText(
        cropped,
        text,
        (text_x, text_y),
        cv2.FONT_HERSHEY_SIMPLEX,
        1.2,
        (255, 255, 255),
        2,
        cv2.LINE_AA,
    )

    # Show cropped frame
    cv2.imshow(window_name, cropped)

    # Check if the 'esc' key is pressed
    if cv2.waitKey(1) == 27:
        break


picam2.stop()
cv2.destroyAllWindows()
