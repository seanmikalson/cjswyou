package com.munity.cjswyou;

public class AudioSampler {

    public short[] downsampleSignal(int factor, short[] audioIn) {
        short[] audioOut = new short[audioIn.length / factor];

        audioIn = applyLowPassFilter(audioIn, 8);

        // Picking out every fth audio byte
        for(int i = 0, j = 0; j < audioOut.length; i++, j+=factor) {
            audioOut[i] = audioIn[j];
        }

        return audioOut;
    }

    private short[] applyLowPassFilter(short[] audioIn, int lowPassFilterFactor) {
        short value = audioIn[0];

        for(int i = 1; i < audioIn.length; i++) {
            short currentValue = audioIn[i];
            value += (currentValue - value) / lowPassFilterFactor;
            audioIn[i] = value;
        }

        return audioIn;
    }
}
