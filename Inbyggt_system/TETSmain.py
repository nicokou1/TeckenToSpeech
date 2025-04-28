import cv2
import numpy as np
from picamera2 import Picamera2
from Xlib import display
from libcamera import Transform
import threading
import time
import requests
from PIL import Image
from io import BytesIO
import json
import base64
import tkinter as tk
from tkinter import messagebox
import sys
import TETSserver

# Global variables
text = "" # Top text
connect = False # is connected to the server

# Mouse callback to detect button click
def mouse_callback(event, x, y, flags, param):
    global text
    global connect
    if event == cv2.EVENT_LBUTTONDOWN:
        # Check if the click is inside the "button" area (x1, y1, x2, y2)
        if button_x1 < x < button_x2 and button_y1 < y < button_y2:
            connect = not(connect)
                

# Get screen resolution
d = display.Display()
screen = d.screen()

screen_width = screen.width_in_pixels
screen_height = screen.height_in_pixels

print(f"Screen Resolution: {screen_width}x{screen_height}")
try:
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

    print(f"Calculated Camera Resolution: {calculated_width}x{calculated_height}")

    # Configure the camera
    config = picam2.create_preview_configuration({"format": "RGB888", "size": (calculated_width, calculated_height)}, transform=Transform(hflip=True))
    picam2.configure(config)
    picam2.start()
except:
    root = tk.Tk()
    root.withdraw()
    messagebox.showinfo("Camera Error.", "Please make sure camera is connected and working.")
    sys.exit()


# OpenCV window setup
window_name = "TeTS Fullscreen"
cv2.namedWindow(window_name, cv2.WND_PROP_FULLSCREEN)
cv2.setWindowProperty(window_name, cv2.WND_PROP_FULLSCREEN, cv2.WINDOW_FULLSCREEN)

# Button area (coordinates for the button)
button_x1 = 25  # Starting X coordinate of the button
button_y1 = 55  # Starting Y coordinate of the button
button_x2 = button_x1 + 275
button_y2 = button_y1 + 50

# Draw button (just a rectangle)
def draw_button(frame):
    global connect
    
    buttontext = ""
    buttoncolor = (0, 0, 0)
    
    if connect:
        buttontext = "Connected"
        buttoncolor = (0, 255, 0)
    else:
        buttontext = "Disconnected"
        buttoncolor = (0, 0, 255)
    
    # Calculate the size of the text
    size = cv2.getTextSize(buttontext, cv2.FONT_HERSHEY_SIMPLEX, 1.2, 2)

# Padding around the text
    padding_x = 20  # Horizontal padding
    padding_y = 10  # Vertical padding

# Set the button size based on text size and padding
    button_width = size[0][0] + padding_x * 2  # Text width + padding on both sides
    button_height = size[0][1] + padding_y * 2  # Text height + padding on both 

# Draw the button (rectangle)
    cv2.rectangle(frame, (button_x1, button_y1), (button_x2, button_y2), buttoncolor, -1)  # Fill button with color

# Place the text in the center of the button
    cv2.putText(frame, buttontext, (button_x1 + padding_x, button_y1 + padding_y + size[0][1]), 
            cv2.FONT_HERSHEY_SIMPLEX, 1.2, (255, 255, 255), 2, cv2.LINE_AA)

# Check if i am connected, or i can even get the status.
if TETSserver.checkConnection(): # TODO REPLACE WITH BUTTON LATER!
    # Start the server thread if i can call the server.
    server_thread = threading.Thread(target=TETSserver.server_method)
    server_thread.daemon = True
    server_thread.start()
else:
    print("Cannot connect to server.")

# Set mouse callback to handle button clicks
cv2.setMouseCallback(window_name, mouse_callback)

while True:
    # Capture frame
    frame = picam2.capture_array()
    height, width = frame.shape[:2]
    y1 = (height - screen_height) // 2
    y2 = y1 + screen_height
    
    # Crop the frame
    frame = frame[y1:y2, :]
    

    # Draw the button
    draw_button(frame)
    
    # Get the size of the text to be drawn
    text_size = cv2.getTextSize(text, cv2.FONT_HERSHEY_SIMPLEX, 1.2, 2)[0]
    text_width, text_height = text_size
    
    # Define text position (center horizontally)
    text_x = (frame.shape[1] - text_width) // 2
    text_y = 40  # Fixed Y position for consistency

    # Add text to the cropped frame
    cv2.putText(frame, text, (text_x, text_y), cv2.FONT_HERSHEY_SIMPLEX,
                1.2, (255, 255, 255), 2, cv2.LINE_AA)
    
    # Show the frame
    cv2.imshow(window_name, frame)
    
    # Check if the 'esc' key is pressed
    if cv2.waitKey(1) == 27:
        break

# Cleanup
picam2.stop()
cv2.destroyAllWindows()
