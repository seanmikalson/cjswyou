package com.munity.cjswyou;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.net.rtp.AudioStream;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import edu.gvsu.masl.echoprint.Codegen;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

public class StreamPlayer {

    private static final String URL_CJSW = "http://stream.cjsw.com/cjsw.mp3";
    MediaPlayer mCjswStream;

    public void startCJSW() throws IOException {
        mCjswStream = new MediaPlayer();
        mCjswStream.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mCjswStream.setDataSource(URL_CJSW);
        mCjswStream.prepare();
        mCjswStream.start();

        /* TODO Figure out how to downsample audio to feed into codegen
        new Thread(new Runnable() {
           public void run() {
               try {
                   readByteStream();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
        }).start();*/

    }

    public void stopCJSW() {
        mCjswStream.stop();
    }

    private void readByteStream() throws IOException {
        URL cjswUrl = new URL(URL_CJSW);

        InputStream cjswIn =cjswUrl.openStream();

        int minBufferSize = AudioTrack.getMinBufferSize(11025, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        AudioTrack radio = new AudioTrack(AudioManager.STREAM_MUSIC, 11025,
                AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);
        //radio.play();

        try {
            Bitstream audioBits = new Bitstream(cjswIn);
            Decoder streamDecoder = new Decoder();

            boolean done = false;
            while(!done) {
                Header frameHead = audioBits.readFrame();
                if(frameHead == null) {
                    Log.e("mytag", "file end");
                    break;
                }

                SampleBuffer sample = (SampleBuffer) streamDecoder.decodeFrame(frameHead, audioBits);
                short[] pcm = sample.getBuffer();
                AudioSampler sampler = new AudioSampler();
                short [] downSample = sampler.downsampleSignal(4, pcm);

                Codegen generator = new Codegen();
                String code = generator.generate(downSample, downSample.length);
                Log.e("mytag", "lengtcode: " + code);


                radio.write(downSample, 0, downSample.length);

                audioBits.closeFrame();
            }
        } catch (BitstreamException e) {

        } catch (DecoderException e) {
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            // Skip bad frames
        }
    }
}
