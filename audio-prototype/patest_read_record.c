/** @file patest_read_record.c
    @ingroup test_src
    @brief Record input into an array; Save array to a file; Playback recorded
    data. Implemented using the blocking API (Pa_ReadStream(), Pa_WriteStream() )
    @author Phil Burk  http://www.softsynth.com
    @author Ross Bencina rossb@audiomulch.com
*/
/*
 * $Id$
 *
 * This program uses the PortAudio Portable Audio Library.
 * For more information see: http://www.portaudio.com
 * Copyright (c) 1999-2000 Ross Bencina and Phil Burk
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * The text above constitutes the entire PortAudio license; however,
 * the PortAudio community also makes the following non-binding requests:
 *
 * Any person wishing to distribute modifications to the Software is
 * requested to send the modifications to the original developer so that
 * they can be incorporated into the canonical version. It is also
 * requested that these non-binding requests be included along with the
 * license above.
 */

#include <stdio.h>
#include <stdlib.h>
#include "portaudio.h"
#include "libs/write_wav.h"
#include <unistd.h>

/* #define SAMPLE_RATE  (17932) // Test failure to open with this value. */
#define SAMPLE_RATE  (44100)
#define FRAMES_PER_BUFFER (512)
#define NUM_SECONDS     (3)
#define NUM_CHANNELS    (1)
/* #define DITHER_FLAG     (paDitherOff)  */
#define DITHER_FLAG     (0) /**/

/* Select sample format. */
#if 0
#define PA_SAMPLE_TYPE  paFloat32
typedef float SAMPLE;
#define SAMPLE_SILENCE  (0.0f)
#define PRINTF_S_FORMAT "%.8f"
#elif 1
#define PA_SAMPLE_TYPE  paInt16
typedef short SAMPLE;
#define SAMPLE_SILENCE  (0)
#define PRINTF_S_FORMAT "%d"
#elif 0
#define PA_SAMPLE_TYPE  paInt8
typedef char SAMPLE;
#define SAMPLE_SILENCE  (0)
#define PRINTF_S_FORMAT "%d"
#else
#define PA_SAMPLE_TYPE  paUInt8
typedef unsigned char SAMPLE;
#define SAMPLE_SILENCE  (128)
#define PRINTF_S_FORMAT "%d"
#endif

/* Write long word data to a little endian format byte array. */
static void WriteLongLE( unsigned char **addrPtr, unsigned long data )
{
    unsigned char *addr = *addrPtr;
    *addr++ =  (unsigned char) data;
    *addr++ =  (unsigned char) (data>>8);
    *addr++ =  (unsigned char) (data>>16);
    *addr++ =  (unsigned char) (data>>24);
    *addrPtr = addr;
}

/* Write short word data to a little endian format byte array. */
static void WriteShortLE( unsigned char **addrPtr,  unsigned short data )
{
    unsigned char *addr = *addrPtr;
    *addr++ =  (unsigned char) data;
    *addr++ =  (unsigned char) (data>>8);
    *addrPtr = addr;
}

/* Write IFF ChunkType data to a byte array. */
static void WriteChunkType( unsigned char **addrPtr, unsigned long cktyp )
{
    unsigned char *addr = *addrPtr;
    *addr++ =  (unsigned char) (cktyp>>24);
    *addr++ =  (unsigned char) (cktyp>>16);
    *addr++ =  (unsigned char) (cktyp>>8);
    *addr++ =  (unsigned char) cktyp;
    *addrPtr = addr;
}

#define WAV_HEADER_SIZE (4 + 4 + 4 + /* RIFF+size+WAVE */ \
        4 + 4 + 16 + /* fmt chunk */ \
        4 + 4 ) /* data chunk */


/*********************************************************************************
 * Open named file and write WAV header to the file.
 * The header includes the DATA chunk type and size.
 * Returns number of bytes written to file or negative error code.
 */
long Audio_WAV_OpenWriter( WAV_Writer *writer, const char *fileName, int frameRate, int samplesPerFrame )
{
    unsigned int  bytesPerSecond;
    unsigned char header[ WAV_HEADER_SIZE ];
    unsigned char *addr = header;
    int numWritten;

    writer->dataSize = 0;
    writer->dataSizeOffset = 0;

    writer->fid = fopen( fileName, "wb" );
    if( writer->fid == NULL )
    {
        return -1;
    }

/* Write RIFF header. */
    WriteChunkType( &addr, RIFF_ID );

/* Write RIFF size as zero for now. Will patch later. */
    WriteLongLE( &addr, 0 );

/* Write WAVE form ID. */
    WriteChunkType( &addr, WAVE_ID );

/* Write format chunk based on AudioSample structure. */
    WriteChunkType( &addr, FMT_ID );
    WriteLongLE( &addr, 16 );
    WriteShortLE( &addr, WAVE_FORMAT_PCM );
        bytesPerSecond = frameRate * samplesPerFrame * sizeof( short);
    WriteShortLE( &addr, (short) samplesPerFrame );
    WriteLongLE( &addr, frameRate );
    WriteLongLE( &addr,  bytesPerSecond );
    WriteShortLE( &addr, (short) (samplesPerFrame * sizeof( short)) ); /* bytesPerBlock */
    WriteShortLE( &addr, (short) 16 ); /* bits per sample */

/* Write ID and size for 'data' chunk. */
    WriteChunkType( &addr, DATA_ID );
/* Save offset so we can patch it later. */
    writer->dataSizeOffset = (int) (addr - header);
    WriteLongLE( &addr, 0 );

    numWritten = fwrite( header, 1, sizeof(header), writer->fid );
    if( numWritten != sizeof(header) ) return -1;

    return (int) numWritten;
}

/*********************************************************************************
 * Write to the data chunk portion of a WAV file.
 * Returns bytes written or negative error code.
 */
long Audio_WAV_WriteShorts( WAV_Writer *writer,
        short *samples,
        int numSamples
        )
{
    unsigned char buffer[2];
    unsigned char *bufferPtr;
    int i;
    short *p = samples;
    int numWritten;
    int bytesWritten;
    if( numSamples <= 0 )
    {
        return -1;
    }

    for( i=0; i<numSamples; i++ )
    {
        bufferPtr = buffer;
        WriteShortLE( &bufferPtr, *p++ );
        numWritten = fwrite( buffer, 1, sizeof( buffer), writer->fid );
        if( numWritten != sizeof(buffer) ) return -1;
    }
    bytesWritten = numSamples * sizeof(short);
    writer->dataSize += bytesWritten;
    return (int) bytesWritten;
}

/*********************************************************************************
 * Close WAV file.
 * Update chunk sizes so it can be read by audio applications.
 */
long Audio_WAV_CloseWriter( WAV_Writer *writer )
{
    unsigned char buffer[4];
    unsigned char *bufferPtr;
    int numWritten;
    int riffSize;

    /* Go back to beginning of file and update DATA size */
    int result = fseek( writer->fid, writer->dataSizeOffset, SEEK_SET );
    if( result < 0 ) return result;

    bufferPtr = buffer;
    WriteLongLE( &bufferPtr, writer->dataSize );
    numWritten = fwrite( buffer, 1, sizeof( buffer), writer->fid );
    if( numWritten != sizeof(buffer) ) return -1;

    /* Update RIFF size */
    result = fseek( writer->fid, 4, SEEK_SET );
    if( result < 0 ) return result;

    riffSize = writer->dataSize + (WAV_HEADER_SIZE - 8);
    bufferPtr = buffer;
    WriteLongLE( &bufferPtr, riffSize );
    numWritten = fwrite( buffer, 1, sizeof( buffer), writer->fid );
    if( numWritten != sizeof(buffer) ) return -1;

    fclose( writer->fid );
    writer->fid = NULL;
    return writer->dataSize;
}

/*******************************************************************/
int main(void);
int main(void)
{
    PaStreamParameters inputParameters, outputParameters;
    PaStream *stream;
    PaError err;
    SAMPLE *recordedSamples;
    int i;
    int totalFrames;
    int numSamples;
    int numBytes;
    int result;
    WAV_Writer writer;
    SAMPLE max, average, val;


    printf("patest_read_record.c\n"); fflush(stdout);

    totalFrames = NUM_SECONDS * SAMPLE_RATE; /* Record for a few seconds. */
    numSamples = totalFrames * NUM_CHANNELS;

    numBytes = numSamples * sizeof(SAMPLE);
    recordedSamples = (SAMPLE *) malloc( numBytes );
    if( recordedSamples == NULL )
    {
        printf("Could not allocate record array.\n");
        exit(1);
    }
    for( i=0; i<numSamples; i++ ) recordedSamples[i] = 0;

    err = Pa_Initialize();
    if( err != paNoError ) goto error;

    inputParameters.device = Pa_GetDefaultInputDevice(); /* default input device */
    if (inputParameters.device == paNoDevice) {
        fprintf(stderr,"Error: No default input device.\n");
        goto error;
    }
    inputParameters.channelCount = NUM_CHANNELS;
    inputParameters.sampleFormat = PA_SAMPLE_TYPE;
    inputParameters.suggestedLatency = Pa_GetDeviceInfo( inputParameters.device )->defaultLowInputLatency;
    inputParameters.hostApiSpecificStreamInfo = NULL;

    /* Record some audio. -------------------------------------------- */
    printf("Recording in 3...\n");
    sleep(1);
    printf("2...\n");
    sleep(1);
    printf("1...\n");
    sleep(1);
    err = Pa_OpenStream(
              &stream,
              &inputParameters,
              NULL,                  /* &outputParameters, */
              SAMPLE_RATE,
              FRAMES_PER_BUFFER,
              paClipOff,      /* we won't output out of range samples so don't bother clipping them */
              NULL, /* no callback, use blocking API */
              NULL ); /* no callback, so no callback userData */
    if( err != paNoError ) goto error;

    err = Pa_StartStream( stream );
    if( err != paNoError ) goto error;
    printf("Now recording!!\n"); fflush(stdout);

    err = Pa_ReadStream( stream, recordedSamples, totalFrames );
    if( err != paNoError ) goto error;

    err = Pa_CloseStream( stream );
    if( err != paNoError ) goto error;

    /* Measure maximum peak amplitude. */
    max = 0;
    average = 0;
    for( i=0; i<numSamples; i++ )
    {
        val = recordedSamples[i];
        if( val < 0 ) val = -val; /* ABS */
        if( val > max )
        {
            max = val;
        }
        average += val;
    }

    average = average / numSamples;

    printf("Sample max amplitude = "PRINTF_S_FORMAT"\n", max );
    printf("Sample average = "PRINTF_S_FORMAT"\n", average );
/*  Was as below. Better choose at compile time because this
    keeps generating compiler-warnings:
    if( PA_SAMPLE_TYPE == paFloat32 )
    {
        printf("sample max amplitude = %f\n", max );
        printf("sample average = %f\n", average );
    }
    else
    {
        printf("sample max amplitude = %d\n", max );
        printf("sample average = %d\n", average );
    }
*/
    /* Write recorded data to a file. */
#if 1
    {
        FILE  *fid;
        fid = fopen("build/recorded.raw", "wb");
        if( fid == NULL )
        {
            printf("Could not open file.");
        }
        else
        {
            fwrite( recordedSamples, NUM_CHANNELS * sizeof(SAMPLE), totalFrames, fid );
            fclose( fid );
            printf("Wrote data to 'recorded.raw'\n");
        }
    }
#endif

#if 1
    {
    result =  Audio_WAV_OpenWriter( &writer, "build/recorded.wav", 44100, 1 );
    if( result < 0 ) goto error_wav;

    result =  Audio_WAV_WriteShorts( &writer, recordedSamples, numSamples);
    if( result < 0 ) goto error_wav;
    printf("Wrote data to 'recorded.wav'\n");

    result =  Audio_WAV_CloseWriter( &writer );
    if( result < 0 ) goto error_wav;

    }
#endif

    /* Playback recorded data.  -------------------------------------------- */

    outputParameters.device = Pa_GetDefaultOutputDevice(); /* default output device */
    if (outputParameters.device == paNoDevice) {
        fprintf(stderr,"Error: No default output device.\n");
        goto error;
    }
    outputParameters.channelCount = NUM_CHANNELS;
    outputParameters.sampleFormat =  PA_SAMPLE_TYPE;
    outputParameters.suggestedLatency = Pa_GetDeviceInfo( outputParameters.device )->defaultLowOutputLatency;
    outputParameters.hostApiSpecificStreamInfo = NULL;

    printf("Begin playback.\n"); fflush(stdout);
    err = Pa_OpenStream(
              &stream,
              NULL, /* no input */
              &outputParameters,
              SAMPLE_RATE,
              FRAMES_PER_BUFFER,
              paClipOff,      /* we won't output out of range samples so don't bother clipping them */
              NULL, /* no callback, use blocking API */
              NULL ); /* no callback, so no callback userData */
    if( err != paNoError ) goto error;

    if( stream )
    {
        err = Pa_StartStream( stream );
        if( err != paNoError ) goto error;
        printf("Waiting for playback to finish.\n"); fflush(stdout);

        err = Pa_WriteStream( stream, recordedSamples, totalFrames );
        if( err != paNoError ) goto error;

        err = Pa_CloseStream( stream );
        if( err != paNoError ) goto error;
        printf("Done.\n"); fflush(stdout);
    }
    free( recordedSamples );

    Pa_Terminate();
    return 0;

error:
    Pa_Terminate();
    fprintf( stderr, "An error occurred while using the portaudio stream\n" );
    fprintf( stderr, "Error number: %d\n", err );
    fprintf( stderr, "Error message: %s\n", Pa_GetErrorText( err ) );
    return -1;

error_wav:
    printf("ERROR: result = %d\n", result );
    return result;
}


