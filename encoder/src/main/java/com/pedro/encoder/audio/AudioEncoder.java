package com.pedro.encoder.audio;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.HandlerThread;
import android.util.Log;
import androidx.annotation.NonNull;
import com.pedro.encoder.BaseEncoder;
import com.pedro.encoder.Frame;
import com.pedro.encoder.input.audio.GetMicrophoneData;
import com.pedro.encoder.utils.CodecUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pedro on 19/01/17.
 *
 * Encode PCM audio data to ACC and return in a callback
 */

public class AudioEncoder extends BaseEncoder implements GetMicrophoneData {

  private static final String TAG = "AudioEncoder";

  private GetAacData getAacData;
  private int bitRate = 64 * 1024;  //in kbps
  private int sampleRate = 32000; //in hz
  private boolean isStereo = true;

  public AudioEncoder(GetAacData getAacData) {
    this.getAacData = getAacData;
  }

  /**
   * Prepare encoder with custom parameters
   */
  public boolean prepareAudioEncoder(int bitRate, int sampleRate, boolean isStereo,
      int maxInputSize) {
    this.sampleRate = sampleRate;
    isBufferMode = true;
    try {
      List<MediaCodecInfo> encoders = new ArrayList<>();
      if (force == CodecUtil.Force.HARDWARE) {
        encoders = CodecUtil.getAllHardwareEncoders(CodecUtil.AAC_MIME);
      } else if (force == CodecUtil.Force.SOFTWARE) {
        encoders = CodecUtil.getAllSoftwareEncoders(CodecUtil.AAC_MIME);
      }

      if (force == CodecUtil.Force.FIRST_COMPATIBLE_FOUND) {
        MediaCodecInfo encoder = chooseAudioEncoder(CodecUtil.AAC_MIME);
        if (encoder != null) {
          codec = MediaCodec.createByCodecName(encoder.getName());
        } else {
          Log.e(TAG, "Valid encoder not found");
          return false;
        }
      } else {
        if (encoders.isEmpty()) {
          Log.e(TAG, "Valid encoder not found");
          return false;
        } else {
          codec = MediaCodec.createByCodecName(encoders.get(0).getName());
        }
      }

      int channelCount = (isStereo) ? 2 : 1;
      MediaFormat audioFormat =
          MediaFormat.createAudioFormat(CodecUtil.AAC_MIME, sampleRate, channelCount);
      audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
      audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, maxInputSize);
      audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE,
          MediaCodecInfo.CodecProfileLevel.AACObjectLC);
      codec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
      running = false;
      return true;
    } catch (IOException | IllegalStateException e) {
      Log.e(TAG, "Create AudioEncoder failed.", e);
      return false;
    }
  }

  public void setForce(CodecUtil.Force force) {
    this.force = force;
  }

  /**
   * Prepare encoder with default parameters
   */
  public boolean prepareAudioEncoder() {
    return prepareAudioEncoder(bitRate, sampleRate, isStereo, 0);
  }

  @Override
  protected void startImp(boolean resetTs) {
    presentTimeUs = System.nanoTime() / 1000;
    handlerThread = new HandlerThread(TAG);
  }

  @Override
  protected void stopImp() {
  }

  @Override
  protected Frame getInputFrame() throws InterruptedException {
    return queue.take();
  }

  @Override
  protected void checkBuffer(@NonNull ByteBuffer byteBuffer,
      @NonNull MediaCodec.BufferInfo bufferInfo) {

  }

  @Override
  protected void sendBuffer(@NonNull ByteBuffer byteBuffer,
      @NonNull MediaCodec.BufferInfo bufferInfo) {
    bufferInfo.presentationTimeUs = System.nanoTime() / 1000 - presentTimeUs;
    getAacData.getAacData(byteBuffer, bufferInfo);
  }

  /**
   * Set custom PCM data.
   * Use it after prepareAudioEncoder(int sampleRate, int channel).
   * Used too with microphone.
   *
   */
  @Override
  public void inputPCMData(Frame frame) {
    if (running && !queue.offer(frame)) {
      Log.i(TAG, "frame discarded");
    }
  }

  private MediaCodecInfo chooseAudioEncoder(String mime) {
    List<MediaCodecInfo> mediaCodecInfoList = CodecUtil.getAllEncoders(mime);
    for (MediaCodecInfo mediaCodecInfo : mediaCodecInfoList) {
      String name = mediaCodecInfo.getName().toLowerCase();
      if (!name.contains("omx.google")) return mediaCodecInfo;
    }
    if (mediaCodecInfoList.size() > 0) {
      return mediaCodecInfoList.get(0);
    } else {
      return null;
    }
  }

  public void setSampleRate(int sampleRate) {
    this.sampleRate = sampleRate;
  }

  public boolean isRunning() {
    return running;
  }

  @Override
  public void formatChanged(@NonNull MediaCodec mediaCodec, @NonNull MediaFormat mediaFormat) {
    getAacData.onAudioFormat(mediaFormat);
  }
}
