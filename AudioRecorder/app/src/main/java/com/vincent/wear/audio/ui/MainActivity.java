package com.vincent.wear.audio.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vincent.wear.audio.R;
import com.vincent.wear.audio.model.AudioChannel;
import com.vincent.wear.audio.model.AudioSampleRate;
import com.vincent.wear.audio.util.Util;
import com.vincent.wear.audio.util.VisualizerHandler;
import com.vincent.wear.audio.view.RecorderVisualizerView;

import java.util.Timer;
import java.util.TimerTask;

import omrecorder.AudioChunk;
import omrecorder.AudioSource;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.Recorder;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

/**
 *
 */
public class MainActivity extends WearableActivity implements View.OnClickListener {

    public static final String NO_AUTHORITY = "没有权限";
    public static final String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final String TAG = "recorder";
    private static final long REPEAT_INTERVAL = 10L;
    private static final int REQ_PERMISSION = 199;
    private static final String TIME_DEFAULT = "00:00:00";
    private Recorder mRecorder = null;
    private boolean mIsRecording = false;
    private ImageView mRecordView = null;
    private RecorderVisualizerView mVisualizerView = null;
    private VisualizerHandler mVisualizerHandler;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    private int mRecorderSecondsElapsed = 0;
    private TextView mTimerView = null;
    private ImageView mList = null;
    private Runnable mUpdateVisualizer = new Runnable() {
        @Override
        public void run() {
            if (mIsRecording) {
                mVisualizerView.invalidate();
                mHandler.postDelayed(this, REPEAT_INTERVAL);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Enables Always-on
        setAmbientEnabled();
        mRecordView = findViewById(R.id.img_record);
        mVisualizerView = findViewById(R.id.visualizer);
        mTimerView = findViewById(R.id.timer);
        mList = findViewById(R.id.list);
        mVisualizerHandler = new VisualizerHandler();
        mRecordView.setOnClickListener(this);
        mList.setOnClickListener(this);

    }

    private void initRecorder() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            Util.ensureDirectory();
            mRecorder = OmRecorder.wav(
                    new PullTransport.Default(getMic(), new PullTransport.OnAudioChunkPulledListener() {
                        @Override
                        public void onAudioChunkPulled(AudioChunk audioChunk) {
                            Log.d(TAG, String.valueOf(audioChunk.maxAmplitude()));
                            float amplitude = mIsRecording ? (float) audioChunk.maxAmplitude() : 0f;
                            mVisualizerView.addAmplitude(amplitude);
                            mHandler.post(mUpdateVisualizer);
                        }
                    }), Util.getAudioFile());
        } else {
            Log.d(TAG, NO_AUTHORITY);
            requestPermissions(PERMISSIONS, REQ_PERMISSION);
        }

    }


    private AudioSource getMic() {
        return Util.getMic(com.vincent.wear.audio.model.AudioSource.MIC, AudioChannel.STEREO, AudioSampleRate.HZ_44100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (null != grantResults) {
            boolean hasPermission = true;
            for (int result : grantResults) {
                if (result != PERMISSION_GRANTED) {
                    hasPermission = false;
                    break;
                }
            }
            if (hasPermission) {
                initRecorder();
            } else {
                Toast.makeText(MainActivity.this, NO_AUTHORITY, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mRecordView) {
            mIsRecording = !mIsRecording;
            if (mIsRecording) {
                initRecorder();
                mRecorder.startRecording();
                startTimer();
            } else {
                mRecorder.stopRecording();
                stopTimer();
            }
            mRecordView.setImageResource(mIsRecording ? R.drawable.stop : R.drawable.begin);
        } else if (v == mList) {
            startActivity(new Intent(this, RecordListActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsRecording) {
            if (null != mRecorder) {
                mRecorder.stopRecording();
            }
        }
        mHandler.removeCallbacks(mUpdateVisualizer);
        mHandler.removeCallbacksAndMessages(null);
    }

    private void startTimer() {
        stopTimer();
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTimer();
            }
        }, 0, 1000);
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        setDefaultTimerDisplay();
    }

    private void setDefaultTimerDisplay() {
        mRecorderSecondsElapsed = 0;
        mTimerView.setText(TIME_DEFAULT);
    }

    private void updateTimer() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mIsRecording) {
                    mRecorderSecondsElapsed++;
                    mTimerView.setText(Util.formatSeconds(mRecorderSecondsElapsed));
                }
            }
        });
    }
}
