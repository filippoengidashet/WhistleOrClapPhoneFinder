/*
 * Copyright (C) 2012 Jacquet Wong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * musicg api in Google Code: http://code.google.com/p/musicg/
 * Android Application in Google Play: https://play.google.com/store/apps/details?id=com.whistleapp
 * 
 */

package org.dalol.phonefinder.model.helper;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class RecorderThread extends Thread {


    private AudioRecord audioRecord;
    private int channelConfiguration;
    private int audioEncoding;
    private int sampleRate;
    private int frameByteSize; // for 1024 fft size (16bit sample size)
    byte[] buffer;

    public RecorderThread() {
        sampleRate = 44100;
        frameByteSize = 1024 * 2;

        channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
        audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

        int recBufSize = AudioRecord.getMinBufferSize(sampleRate,
                channelConfiguration, audioEncoding); // need to be larger than
        // size of a frame
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate, channelConfiguration, audioEncoding, recBufSize);
        buffer = new byte[frameByteSize];
    }

    public AudioRecord getAudioRecord() {
        return audioRecord;
    }

    public boolean isRecording() {
        if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            return true;
        }

        return false;
    }

    public void startRecording() {
        try {
            audioRecord.startRecording();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        try {
            audioRecord.stop();
            audioRecord.release();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getMaxAmplitude() {
        int amplitude = (buffer[0] & 0xff) << 8 | buffer[1];
        return Math.abs(amplitude);
    }

    public byte[] getFrameBytes() {
        audioRecord.read(buffer, 0, frameByteSize);

        // analyze sound
        int totalAbsValue = 0;
        short sample = 0;
        float averageAbsValue = 0.0f;

        for (int i = 0; i < frameByteSize; i += 2) {
            sample = (short) ((buffer[i]) | buffer[i + 1] << 8);
            totalAbsValue += Math.abs(sample);
        }
        averageAbsValue = totalAbsValue / frameByteSize / 2;

        Log.e("", "averageAbsValue : " + averageAbsValue);

        // no input
        if (averageAbsValue < 30) {
            return null;
        }

        return buffer;
    }

}