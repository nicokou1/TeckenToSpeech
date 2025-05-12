import cv2
import picamera2
import server_handler
from libcamera import Transform
from Xlib import display


class screen_handler:
    """This class handles the frame capture and the touch screen
    ;==========================================
    ; Author: Viktor Vallmark, David Hornemark
    ; Date:   12-05-2025
    ;==========================================
    """

    text: str
    connected: bool
    camera: picamera2
    serverhandler: server_handler

    def __init__(
        self, connected: bool = False, format: str = "RGB888", url: str = "10.0.0.1"
    ) -> None:
        self.connected = connected
        self.serverhandler.set_url(self, url=url)
        self.initialize(format=format)

    def set_text(self, text: str) -> None:
        self.text = text

    def set_connected(self, connected: bool) -> None:
        self.connected = connected

    def initialize(
        self,
        format: str,
    ) -> None:
        screen = display.Display.screen()
        screen_width: float = screen.width_in_pixels
        screen_height: float = screen.height_in_pixels
        self.camera = Picamera2()
        config = self.camera.create_preview_configuration(
            {"format": format, "size": (int(screen_width), int(screen_height))},
            transform=Transform(hflip=True),
        )
        try:
            self.camera.configure(config)
            self.camera.start()
        except Exception as e:
            print(e)

    async def capture_frame(self, window_name: str) -> str:

        try:
            frame = self.camera.capture_array()
        except IOError as err:
            print(err)

        cv2.namedWindow(window_name, cv2.WND_PROP_FULLSCREEN)
        cv2.setWindowProperty(cv2.WND_PROP_FULLSCREEN, cv2.WINDOW_FULLSCREEN)

        cv2.imshow(window_name, frame)

        # TODO: This function needs to call OpenCV

        await self.send_frame("k")
        return ""

    async def send_frame(self, letter_to_send: str) -> None:
        # TODO: Fix the sending of the frame to the server
        try:
            await server_handler.send_json_to_server(self, letter_to_send)
        except ConnectionError as e:
            print(e)

    def cleanup(self) -> None:
        if self.camera is not None:
            self.camera.stop()
            cv2.destroyAllWindows()

    def __exit__(self):
        self.cleanup()
