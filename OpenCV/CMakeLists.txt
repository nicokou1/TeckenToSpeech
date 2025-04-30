cmake_minimum_required(VERSION 3.12)
project(HandGestureOpenCV)

find_package(OpenCV REQUIRED COMPONENTS core imgproc highgui videoio)
add_executable(video_handler video.cpp)
target_link_libraries(hand_gesture ${OpenCV_LIBS})
