import tkinter as tk
from PIL import Image, ImageTk
import cv2
from picamera2 import Picamera2
from Xlib import display
import threading

# Setup Tkinter Fullscreen Window
root = tk.Tk()
root.attributes("-fullscreen", True)
root.configure(bg="black")
root.bind("<Escape>", lambda e: root.destroy())

# Setup a Label widget for camera feed
video_label = tk.Label(root, bg="black")
video_label.pack(expand=True)

# Get screen resolution using Xlib
d = display.Display()
screen = d.screen()
screen_width = screen.width_in_pixels
screen_height = screen.height_in_pixels

# Setup camera
picam2 = Picamera2()
camera_resolution = picam2.preview_configuration.main.size
camera_width = camera_resolution[0]
camera_height = camera_resolution[1]

# Calculate resolution to fit screen width
camera_multiplier = screen_width / camera_width
calculated_width = int(camera_width * camera_multiplier)
calculated_height = int(camera_height * camera_multiplier)

# Create and start camera config
config = picam2.create_preview_configuration({"format": "RGB888", "size": (calculated_width, calculated_height)})
picam2.configure(config)
picam2.start()

# Global variable for frame
frame = None

# Thread function to capture frames continuously
def capture_frame():
    global frame
    while True:
        frame = picam2.capture_array()

# Start the capture thread
capture_thread = threading.Thread(target=capture_frame, daemon=True)
capture_thread.start()

# Frame update function
def update_frame():
    global frame
    if frame is not None:
        height, width = frame.shape[:2]

        # Vertically crop the center of the frame to fit the screen height
        y1 = max((height - screen_height) // 2, 0)
        y2 = y1 + screen_height
        cropped = frame[y1:y2, :]

        # Overlay text
        cv2.putText(cropped, "TeTS Feed", (10, 40), cv2.FONT_HERSHEY_SIMPLEX,
                    1.2, (255, 255, 255), 2, cv2.LINE_AA)

        # Convert BGR (OpenCV) to RGB (Pillow)
        image = cv2.cvtColor(cropped, cv2.COLOR_BGR2RGB)
        image = Image.fromarray(image)
        photo = ImageTk.PhotoImage(image=image)

        # Update label
        video_label.imgtk = photo
        video_label.config(image=photo)

    # Repeat
    root.after(33, update_frame)  # 33 ms = ~30 FPS

# Start updating
update_frame()
root.mainloop()

# Cleanup
picam2.stop()
