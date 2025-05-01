import os
import mmap
import posix_ipc
import numpy as np
import picamera2



class frame_producer:
    def __init__(self, resolution=(640,480), picformat="RGB888",
                 shm_name="/cv_frame", sem_empty="/sem_empty",
                 sem_full="/sem_full"):
        self.width, self.height = resolution
        self.picformat = picformat
        self.shm_name = shm_name
        self.sem_empty = sem_empty
        self.sem_full = sem_full
        self.picam2 = picamera2.Picamera2()
        self.config = self.picam2.create_video_configuration(main={"size": resolution, "format": picformat})
        self.setup_shared_memory()
        self.setup_semaphores()

    def setup_shared_memory(self):
        """
        hello
        """
        self.channels = 3
        self.header_size = 12
        self.frame_size = self.width * self.height * self.channels

        try:
            self.shm = posix_ipc.SharedMemory(
                self.shm_name,
                flags=posix_ipc.O_CREAT,
                size=self.header_size + self.frame_size
            )
            self.shm_fd = os.fdopen(self.shm.fd, 'rb+')
            self.shm_map = mmap.mmap(self.shm_fd.fileno(), self.header_size + self.frame_size)
            metadata = np.array([self.width, self.height, self.channels], dtype=np.int32)
            self.shm_map.seek(0)
            self.shm_map.write(metadata.tobytes())
        except Exception as e:
            self.close()
            raise RuntimeError(f"Shared memory failed: {str(e)}")

    def setup_semaphores(self):
        try:
            self.sem_empty = posix_ipc.Semaphore(
                self.sem_empty_name,
                flags=posix_ipc.O_CREAT,
                initial_value=1
            )
            self.sem_full = posix_ipc.Semaphore(
                self.sem_full_name,
                flags=posix_ipc.O_CREAT,
                initial_value=0
            )
        except Exception as e:
            self.close()
            raise RuntimeError(f"Semaphore setup failed: {str(e)}")


    def start_capture(self):
        self.picam2.configure(self.config)
        self.picam2.start()
        return self

    def capture_loop(self):
        """ 
        for frame in camera.capture_loop():
            Do something here
            pass
        """
        try:
            while True:
                frame = self.picam2.capture_array()
                self.sem_empty.acquire()
                
                self.shm_map.seek(self.header_size)
                self.shm_map.write(frame.tobytes())
                
                self.sem_full.release()
                yield frame                

        except KeyboardInterrupt:
            self.close()
        except Exception as e:
            self.close()
            raise RuntimeError(f"Capture loop failed: {str(e)}")

    def close(self):
        if hasattr(self, 'picam2'):
            self.picam2.stop()
            self.picam2.close()
        if hasattr(self, 'shm_map'):
            self.shm_map.close()
        if hasattr(self, 'shm_fd'):
            self.shm_fd.close()
        if hasattr(self, 'shm'):
            self.shm.unlink()
        if hasattr(self, 'sem_empty'):
            self.sem_empty.unlink()
        if hasattr(self, 'sem_full'):
            self.sem_full.unlink()

    def __enter__(self):
        self.start_capture()
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.close()
