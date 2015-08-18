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

import java.util.LinkedList;

import com.musicg.api.ClapApi;
import com.musicg.api.DetectionApi;
import com.musicg.api.WhistleApi;
import com.musicg.wave.WaveHeader;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

public class DetectorThread extends Thread {


    private DetectorType mType;
    private RecorderThread recorder;
    private WaveHeader waveHeader;
    private DetectionApi mDetectionApi;
    private Thread _thread;

    private LinkedList<Boolean> whistleResultList = new LinkedList<Boolean>();
    private int numWhistles;
    private int totalWhistlesDetected = 0;
    private int whistleCheckLength = 3;
    private int whistlePassScore = 3;
    private boolean isSound;

    public DetectorThread(RecorderThread recorder, DetectorType type) {
        mType = type;
        this.recorder = recorder;
        AudioRecord audioRecord = recorder.getAudioRecord();

        int bitsPerSample = 0;
        if (audioRecord.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT) {
            bitsPerSample = 16;
        } else if (audioRecord.getAudioFormat() == AudioFormat.ENCODING_PCM_8BIT) {
            bitsPerSample = 8;
        }

        int channel = 0;

        if (audioRecord.getChannelConfiguration() == AudioFormat.CHANNEL_IN_MONO) {
            channel = 1;
        }

        waveHeader = new WaveHeader();
        waveHeader.setChannels(channel);
        waveHeader.setBitsPerSample(bitsPerSample);
        waveHeader.setSampleRate(audioRecord.getSampleRate());

        switch (type) {
            case CLAP:
                mDetectionApi = new ClapApi(waveHeader);
                break;
            case WHISTLE:
                mDetectionApi = new WhistleApi(waveHeader);
                break;
        }
    }

    private void initBuffer() {
        numWhistles = 0;
        whistleResultList.clear();

        // init the first frames
        for (int i = 0; i < whistleCheckLength; i++) {
            whistleResultList.add(false);
        }
        // end init the first frames
    }

    public void start() {
        _thread = new Thread(this);
        _thread.start();
    }

    public void stopDetection() {
        _thread = null;
    }

    @Override
    public void run() {
        Log.e("", "DetectorThread started...");

        try {
            byte[] buffer;
            initBuffer();

            Thread thisThread = Thread.currentThread();
            while (_thread == thisThread) {
                // detect sound
                buffer = recorder.getFrameBytes();

                Log.d("", "recorder.getFrameBytes() " + buffer);

                // audio analyst
                if (buffer != null) {
                    // sound detected
                    // MainActivity.whistleValue = numWhistles;

                    // whistle detection
                    // System.out.println("*Whistle:");

                    try {

                        switch (mType) {
                            case CLAP:

                                boolean isClap = ((ClapApi) mDetectionApi).isClap(buffer);
                                isSound = isClap;

                                Log.e("", "isClap : " + isClap + " "+ buffer.length);

                                break;
                            case WHISTLE:
                                boolean isWhistle = ((WhistleApi) mDetectionApi).isWhistle(buffer);
                                isSound = isWhistle;
                                Log.e("", "isWhistle : " + isWhistle + " "+ buffer.length);
                                break;
                        }

                        if (whistleResultList.getFirst()) {
                            numWhistles--;
                        }

                        whistleResultList.removeFirst();
                        whistleResultList.add(isSound);

                        if (isSound) {
                            numWhistles++;
                        }

                        Log.e("", "numWhistles : " + numWhistles);

                        if (numWhistles >= whistlePassScore) {
                            // clear buffer
                            initBuffer();
                            totalWhistlesDetected++;

                            Log.e("", "totalWhistlesDetected : "
                                    + totalWhistlesDetected);

                            if (onSoundListener != null) {
                                onSoundListener.onSound(mType);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.w("", "error " + e.getMessage()+" Cause :: " + e.getCause());
                    }
                    // end whistle detection
                } else {
                    // Debug.e("", "no sound detected");
                    // no sound detected
                    if (whistleResultList.getFirst()) {
                        numWhistles--;
                    }
                    whistleResultList.removeFirst();
                    whistleResultList.add(false);

                    // MainActivity.whistleValue = numWhistles;
                }
                // end audio analyst
            }

            Log.e("", "Terminating detector thread...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private OnSoundListener onSoundListener;

    public void setOnSoundListener(OnSoundListener onSoundListener) {
        this.onSoundListener = onSoundListener;
    }

    public interface OnSoundListener {
        void onSound(DetectorType type);
    }

    public int getTotalWhistlesDetected() {
        return totalWhistlesDetected;
    }
}