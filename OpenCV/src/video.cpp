#include <cstddef>
#include <opencv2/core.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/video.hpp>

class PictureHandler {

public:
  PictureHandler();
  ~PictureHandler();

  void blurImage(std::byte *input) { cv::Mat blur, mask; }
};

int main(int argc, char **argv) { return 0; }
