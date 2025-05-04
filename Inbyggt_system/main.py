import Inbyggt_system.producer_frames
import numpy as np



def main():
    producer = Inbyggt_system.producer_frames.frame_producer()
    
    for frame in producer.capture_loop():
        print(frame)


if __name__ == "__main__":
    main()
