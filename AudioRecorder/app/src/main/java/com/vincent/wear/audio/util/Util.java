package com.vincent.wear.audio.util;

import android.graphics.Color;
import android.media.AudioFormat;
import android.os.Environment;
import android.os.Handler;

import com.vincent.wear.audio.model.AudioChannel;
import com.vincent.wear.audio.model.AudioSampleRate;
import com.vincent.wear.audio.model.AudioSource;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;


public class Util {
    public static final String AUDIO_DIRECTORY = "AudioRecord";
    public static final String WAV_FORMAT = ".wav";
    private static final Handler HANDLER = new Handler();

    private Util() {
    }

    public static void wait(int millis, Runnable callback) {
        HANDLER.postDelayed(callback, millis);
    }

    public static omrecorder.AudioSource getMic(AudioSource source,
                                                AudioChannel channel,
                                                AudioSampleRate sampleRate) {
        return new omrecorder.AudioSource.Smart(
                source.getSource(),
                AudioFormat.ENCODING_PCM_16BIT,
                channel.getChannel(),
                sampleRate.getSampleRate());
    }

    public static boolean isBrightColor(int color) {
        if (android.R.color.transparent == color) {
            return true;
        }
        int[] rgb = {Color.red(color), Color.green(color), Color.blue(color)};
        int brightness = (int) Math.sqrt(
                rgb[0] * rgb[0] * 0.241 +
                        rgb[1] * rgb[1] * 0.691 +
                        rgb[2] * rgb[2] * 0.068);
        return brightness >= 200;
    }

    public static int getDarkerColor(int color) {
        float factor = 0.8f;
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.argb(a,
                Math.max((int) (r * factor), 0),
                Math.max((int) (g * factor), 0),
                Math.max((int) (b * factor), 0));
    }

    public static String formatSeconds(int seconds) {
        return getTwoDecimalsValue(seconds / 3600) + ":"
                + getTwoDecimalsValue(seconds / 60) + ":"
                + getTwoDecimalsValue(seconds % 60);
    }

    private static String getTwoDecimalsValue(int value) {
        if (value >= 0 && value <= 9) {
            return "0" + value;
        } else {
            return value + "";
        }
    }


    public static String getDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
        return df.format(System.currentTimeMillis());
    }


    public static  String getStoreDirectory()
    {
        return Environment.getExternalStorageDirectory() + File.separator + AUDIO_DIRECTORY + File.separator;
    }
    public static void ensureDirectory() {
        File file = new File(getStoreDirectory());
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static  File getAudioFile() {
        return new File(getStoreDirectory(), getDate() + WAV_FORMAT);
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.0");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

}