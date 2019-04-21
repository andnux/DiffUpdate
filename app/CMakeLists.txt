cmake_minimum_required(VERSION 3.2)

include_directories(src/main/cpp)

file(GLOB CPP src/main/cpp/*.c)

add_library(bspatch SHARED ${CPP})

target_link_libraries(bspatch log)
