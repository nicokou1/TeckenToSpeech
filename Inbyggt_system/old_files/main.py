import Inbyggt_system.old_files.producer_frames


def main():
    producer = Inbyggt_system.old_files.producer_frames.frame_producer()
    
    for frame in producer.capture_loop():
        print(frame)


if __name__ == "__main__":
    main()
