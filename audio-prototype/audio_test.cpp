/*
Author: Nam Duong
Date: October 11, 2026

This is for testing if I can get a microphone input.

To run, copy this command into the terminal
cd audio-prototype
cmake --build build
./build/audio_test

To run audio output test,
cd audio-prototype
gcc patest_sine8.c libs/libportaudio.so.2.0.0 -lrt -lm -lasound -ljack -pthread -o build/sound_test
./build/sound_test
It should play a single frequency sine wave for a few seconds, then does it again

To run recording test, 
cd audio-prototype
gcc patest_read_record.c libs/libportaudio.so.2.0.0 -lrt -lm -lasound -ljack -pthread -o build/mic_test
./build/mic_test
It should record for 5 seconds, then playback the recording
If error, please inform Nam about it 
*/

#include <iostream>
#include "portaudio.h"
using namespace std;

int main() {
    cout << "Hello World\n";
    cout << "Relearning how to do shit in C++ gimme a sec\n";

    auto x = 5;
    auto y = 3;

    auto z = 5*3;
    cout << z << " if this equals 15 then hallelujah\n";
}