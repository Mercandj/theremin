#include "WavGenerator.h"
#include <string>
#include <malloc.h>
#include <cstring>
#include "AudioManager.h"

static const long MAX_FILE_SIZE = 30000000;

static float shortToFloat(int16_t i);

static float cleanFloat(float f);

WavGenerator::WavGenerator() {
}

void WavGenerator::render(int16_t *buffer, int32_t channelStride, int32_t numFrames) {
    // TODO
}

void WavGenerator::render(float *buffer, int32_t channelCount, int32_t numFrames) {
    if (channelCount > 2) {
        throw;
    }
    if (volume < 0.2 || !loaded || currentPositionL[lastPlayedId] < 0 ||
        currentPositionR[lastPlayedId] < 0) {
        memset(buffer, 0, sizeof(float) * channelCount * numFrames);
        return;
    }
    for (int i = 0; i < numFrames * channelCount; i++) {
        if (i % 2 == 0) {
            long channelBufferPosition = currentPositionL[lastPlayedId];
            if (channelBufferPosition < 0) {
                buffer[i] = 0;
            } else {
                buffer[i] = cleanFloat(
                        shortToFloat(buffers[lastPlayedId][channelBufferPosition]) * volume);
                currentPositionL[lastPlayedId] += channelCount;
            }
        } else {
            long channelBufferPosition = currentPositionR[lastPlayedId];
            if (channelBufferPosition < 0) {
                buffer[i] = 0;
            } else {
                buffer[i] = cleanFloat(
                        shortToFloat(buffers[lastPlayedId][channelBufferPosition]) * volume);
                currentPositionR[lastPlayedId] += channelCount;
            }
        }
    }
    if (currentPositionL[lastPlayedId] >= sizes[lastPlayedId]) {
        currentPositionL[lastPlayedId] = -1;
    }
    if (currentPositionR[lastPlayedId] >= sizes[lastPlayedId]) {
        currentPositionR[lastPlayedId] = -1;
    }
}

const void WavGenerator::load(const char **filePaths, int nbFilePaths) {
    buffers = (int16_t **) calloc((size_t) nbFilePaths, sizeof(int16_t *));
    sizes = (long *) calloc((size_t) nbFilePaths, sizeof(long));
    currentPositionL = (long *) calloc((size_t) nbFilePaths, sizeof(long));
    currentPositionR = (long *) calloc((size_t) nbFilePaths, sizeof(long));
    for (int i = 0; i < nbFilePaths; i++) {
        buffers[i] = (int16_t *) calloc((size_t) MAX_FILE_SIZE, sizeof(int16_t));
        sizes[i] = extractWav(filePaths[i], i);
        currentPositionL[i] = -1;
        currentPositionR[i] = -1;
    }
    loaded = true;
}

const void WavGenerator::play(int id) {
    lastPlayedId = id;
    currentPositionL[id] = 0;
    currentPositionR[id] = 1;
}

long WavGenerator::extractWav(const char *filePath, int id) {
    FILE *fp = fopen(filePath, "rb");
    if (fp == NULL) {
        return -1;
    }
    fseek(fp, 44, 0);
    long size = fread(buffers[id], sizeof(short), MAX_FILE_SIZE, fp);
    fclose(fp);
    return size;
}

static float shortToFloat(int16_t i) {
    float f;
    f = ((float) i) / (float) 32768;
    if (f > 1) f = 1;
    if (f < -1) f = -1;
    return f;
}

static float cleanFloat(float f) {
    if (f > 1) f = 1;
    if (f < -1) f = -1;
    return f;
}
