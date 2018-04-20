#ifndef SAMPLER_WAV_GENERATOR_H
#define SAMPLER_WAV_GENERATOR_H

#include <math.h>
#include <cstdint>

class WavGenerator {
public:
    WavGenerator();

    ~WavGenerator() = default;

    void render(int16_t *buffer, int32_t channelStride, int32_t numFrames);

    void render(float *buffer, int32_t channelStride, int32_t numFrames);

    const void load(const char **filePaths, int nbFilePaths);

    const void play(int id);

    inline void setVolume(float volume) {
        this->volume = volume;
    }

private:
    long *currentPositions;
    int16_t **buffers;
    long *sizes;
    bool loaded = false;
    int nbSamples = 0;

    float volume = 1;

    long extractWav(const char *filePath, int id);
};

#endif /* SAMPLER_WAV_GENERATOR_H */
