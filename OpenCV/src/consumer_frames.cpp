#include <cstdint>
#include <cstring>
#include <fcntl.h>
#include <iostream>
#include <opencv4/opencv2/opencv.hpp>
#include <semaphore.h>
#include <sys/mman.h>
#include <system_error>
#include <unistd.h>

/*
 *
 * This module takes data from shared memory so that it can
 * be processed in OpenCV. It handles synchronization with semaphores
 * Usage: Instantiate a FrameConsumer object and have it call process_loop().
 *
 * @Author Viktor Vallmark
 * * * */

#pragma pack(push, 1)
struct FrameMetadata {
  int width;
  int height;
  int channels;
};
#pragma pack(pop)

struct SharedMemoryData {
  int fd = -1;
  void *ptr = MAP_FAILED;
  size_t size = 0;
  FrameMetadata metadata{};
  const char *name;

  uint8_t *frame_buffer() const {
    return static_cast<uint8_t *>(ptr) + sizeof(FrameMetadata);
  }
};

struct SemaphoreData {
  sem_t *empty = SEM_FAILED;
  sem_t *full = SEM_FAILED;
  const char *empty_name;
  const char *full_name;
};

class SharedMemoryHandler {

private:
  SharedMemoryData data;

public:
  explicit SharedMemoryHandler(const char *name) {
    data.name = name;

    data.fd = shm_open(data.name, O_RDWR, 0666);
    if (data.fd == -1) {
      throw std::system_error(errno, std::system_category(), "shm_open failed");
    }

    FrameMetadata temp_meta;
    if (read(data.fd, &temp_meta, sizeof(FrameMetadata)) !=
        sizeof(FrameMetadata)) {
      close(data.fd);
      throw std::runtime_error("Failed to read metadata");
    }

    data.metadata = temp_meta;
    data.size = sizeof(FrameMetadata) +
                (temp_meta.width * temp_meta.height * temp_meta.channels);

    data.ptr = mmap(0, data.size, PROT_READ, MAP_SHARED, data.fd, 0);
    if (data.ptr == MAP_FAILED) {
      close(data.fd);
      throw std::system_error(errno, std::system_category(), "mmap failed");
    }
  }

  ~SharedMemoryHandler() {
    if (data.ptr != MAP_FAILED)
      munmap(data.ptr, data.size);
    if (data.fd != -1)
      close(data.fd);
  }

  const FrameMetadata &metadata() const { return data.metadata; }
  const uint8_t *frame_data() const { return data.frame_buffer(); }
};

class SemaphoreHandler {

private:
  void cleanup() {
    if (data.empty != SEM_FAILED)
      sem_close(data.empty);
    if (data.full != SEM_FAILED)
      sem_close(data.full);
  }

  SemaphoreData data;

public:
  SemaphoreHandler(const char *empty_name, const char *full_name) {
    data.empty_name = empty_name;
    data.full_name = full_name;

    data.empty = sem_open(empty_name, 0);
    data.full = sem_open(full_name, 0);

    if (data.empty == SEM_FAILED || data.full == SEM_FAILED) {
      cleanup();
      throw std::system_error(errno, std::system_category(), "sem_open failed");
    }
  }

  ~SemaphoreHandler() { cleanup(); }

  void wait_full() { sem_wait(data.full); }
  void post_empty() { sem_post(data.empty); }
};

class FrameConsumer {

private:
  SharedMemoryHandler shm_;
  SemaphoreHandler sem_;
  cv::Mat frame_;

  cv::Mat create_frame() const {
    const auto &meta = shm_.metadata();
    return cv::Mat(meta.height, meta.width, CV_8UC(meta.channels));
  }
  // This function is going to get replaced. It's only for debugging
  void show_frame() {
    cv::imshow("Frame", frame_);
    cv::waitKey(1);
  }

  const std::string &process_frame() {
    // TODO: implement the processing logic here

    cv::Mat blur, mask, finished;
  }

public:
  FrameConsumer(const char *shm_name, const char *sem_empty,
                const char *sem_full)
      : shm_(shm_name), sem_(sem_empty, sem_full), frame_(create_frame()) {}

  void process_loop() {
    while (true) {
      sem_.wait_full();

      memcpy(frame_.data, shm_.frame_data(),
             shm_.metadata().width * shm_.metadata().height *
                 shm_.metadata().channels);

      process_frame();

      show_frame();

      sem_.post_empty();

      // TODO: Right now you have to stop the loop by CTRL+C. Add a better way
      // of handling this without having to use cv::waitKey() every iteration
    }
  }
  // TODO: Maybe need to implement a custom destructor
};

int main() {
  try {
    FrameConsumer consumer("/cv_frame", "/sem_empty", "/sem_full");
    consumer.process_loop();
  } catch (const std::exception &e) {
    std::cerr << "Error: " << e.what() << std::endl;
    return 1;
  }
  return 0;
}
