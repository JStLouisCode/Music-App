cmake_minimum_required(VERSION 3.23)

project(audio_test)

add_executable(audio_test)
target_sources(audio_test
  PRIVATE
    audio_test.cpp
)

add_library(PortAudio)
target_sources(PortAudio
  PRIVATE
    test.cpp
    libs/libportaudio.a
    patest_read_record.c
    
  PUBLIC
    FILE_SET HEADERS
    FILES
      portaudio.h
)