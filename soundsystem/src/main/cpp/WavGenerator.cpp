#include "WavGenerator.h"
#include <string>
#include <malloc.h>
#include <cstring>
#include "AudioManager.h"

static const long MAX_FILE_SIZE = 30000000;

static float shortToFloat(int16_t i);

WavGenerator::WavGenerator() {
}

void WavGenerator::render(int16_t *buffer, int32_t channelStride, int32_t numFrames) {
    // TODO
}

void WavGenerator::render(float *buffer, int32_t channelCount, int32_t numFrames) {
    if (channelCount > 2) {
        throw;
    }
    memset(buffer, 0, sizeof(float) * channelCount * numFrames);
    if (volume < 0.1 || !loaded) {
        return;
    }
    for (int frame = 0; frame < numFrames * channelCount; frame++) {
        float outputFrame = 0;
        for (int sampleIndex = 0; sampleIndex < nbSamples; sampleIndex++) {
            long channelBufferPosition = currentPositions[sampleIndex];
            if (channelBufferPosition >= 0) {
                outputFrame += shortToFloat(buffers[sampleIndex][channelBufferPosition]);
                currentPositions[sampleIndex]++;
            }
        }
        buffer[frame] = outputFrame * volume;
    }
    for (int sampleIndex = 0; sampleIndex < nbSamples; sampleIndex++) {
        if (currentPositions[sampleIndex] >= sizes[sampleIndex]) {
            currentPositions[sampleIndex] = -1;
        }
    }
}

const void WavGenerator::load(const char **filePaths, int nbFilePaths) {
    nbSamples = nbFilePaths;
    buffers = (int16_t **) calloc((size_t) nbFilePaths, sizeof(int16_t *));
    sizes = (long *) calloc((size_t) nbFilePaths, sizeof(long));
    currentPositions = (long *) calloc((size_t) nbFilePaths, sizeof(long));
    for (int i = 0; i < nbFilePaths; i++) {
        buffers[i] = (int16_t *) calloc((size_t) MAX_FILE_SIZE, sizeof(int16_t));
        sizes[i] = extractWav(filePaths[i], i);
        currentPositions[i] = -1;
    }
    loaded = true;
}

const void WavGenerator::play(int id) {
    currentPositions[id] = 0;
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

