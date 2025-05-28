cmake_minimum_required(VERSION 3.12)
project(TeckenToSpeech)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

find_package(OpenCV REQUIRED)

add_executable(consumer_frames
    ./src/consumer_frames.cpp
)

target_include_directories(consumer_frames PRIVATE
    ${OpenCV_INCLUDE_DIRS}
)

target_link_libraries(consumer_frames PRIVATE
    ${OpenCV_LIBS}
    rt      
    pthread 
)

if(UNIX AND NOT APPLE)
    target_compile_options(consumer_frames PRIVATE
        -Wall
        -Wextra
        -Werror
    )
endif()
