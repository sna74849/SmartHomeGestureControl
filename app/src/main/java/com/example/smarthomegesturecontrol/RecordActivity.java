package com.example.smarthomegesturecontrol;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.icu.text.SimpleDateFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class RecordActivity extends AppCompatActivity {
    private String item = "";
    private int mCount;
    private TextureView mTextureView;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private CameraDevice mCameraDevice;
    private CameraManager mCameraManager;
    private CameraCaptureSession mCameraCaptureSession;
    private File mVideoFolder;
    private String mVideoFileName;
    private EditText mTimerText;
    private Timer mTimer;
    private TimerTask mTimerTask;

    private MediaRecorder mMediaRecorder = new MediaRecorder();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        // Getting selected item.
        item = getIntent().getStringExtra("item");

        prepareCameraDevice();
        createRecord();
    }

    private void prepareCameraDevice(){
        /* Checking and requesting permission */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( new String[] {Manifest.permission.CAMERA}, 0);
            return;
        }

        /* Creating CameraManager */
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        /* Getting CameraId */
        String[] cameraIdList = new String[0];
        try {
            cameraIdList = mCameraManager.getCameraIdList();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        String mCameraId = null;
        for (String cameraId : cameraIdList) {
            CameraCharacteristics characteristics = null;
            try {
                characteristics = mCameraManager.getCameraCharacteristics(cameraId);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            switch (characteristics.get(CameraCharacteristics.LENS_FACING)) {
                case CameraCharacteristics.LENS_FACING_FRONT:

                    break;
                case CameraCharacteristics.LENS_FACING_BACK:
                    mCameraId = cameraId;
                    break;
                default:
            }
        }

        /* CameraCallBack */
        CameraDevice.StateCallback cdscbck = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(CameraDevice cameraDevice) {
                mCameraDevice = cameraDevice;
                createCameraPreviewSession();
            }
            @Override
            public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                cameraDevice.close();
                mCameraDevice = null;
            }
            @Override
            public void onError(@NonNull CameraDevice cameraDevice, int error) {
                cameraDevice.close();
                mCameraDevice = null;
            }
        };
        /* CameraOpening */
        try {
            mCameraManager.openCamera(mCameraId, cdscbck, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createRecordPreviewSession(){
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            texture.setDefaultBufferSize(1080, 1920);
            Surface surface = new Surface(texture);
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            mPreviewRequestBuilder.addTarget(surface);
            Surface recordSurface = mMediaRecorder.getSurface();
            mPreviewRequestBuilder.addTarget(recordSurface);

            mCameraDevice.createCaptureSession(Arrays.asList(surface,recordSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            try {
                                mCameraCaptureSession = cameraCaptureSession;
                                mCameraCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), null, null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                            Toast.makeText(getApplicationContext(),"Session Error",Toast.LENGTH_LONG).show();
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCameraPreviewSession() {
        mTextureView = (TextureView) findViewById(R.id.textureView);
        mTextureView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            texture.setDefaultBufferSize(1080, 1920);
            Surface surface = new Surface(texture);
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            mCameraDevice.createCaptureSession(Arrays.asList(surface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            try {
                                mCameraCaptureSession = cameraCaptureSession;
                                mCameraCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), null, null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                            Toast.makeText(getApplicationContext(),"Session Error",Toast.LENGTH_LONG).show();
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createRecord(){
        findViewById(R.id.record).setOnClickListener(v -> {

            /* Checking and requesting permission */
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions( new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            }

            createVideoFolder();
            try {
                createVideoFileName();
                setupMediaRecorder();
            } catch (IOException e) {
                e.printStackTrace();
            }
            createRecordPreviewSession();
            mMediaRecorder.start();
            findViewById(R.id.record).setClickable(false);
            mTimerText = findViewById(R.id.editTextTime);
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(() -> {
                        mTimerText.setText(String.format("%1$02d:%2$02d:%3$02d",0,0,mCount));
                        mCount++;
                        if (mCount > 5 ) stopRecording();
                    });
                }
            };
            mTimer.schedule(mTimerTask,0,1000);
        });
    }

    private void stopRecording(){
        mTimer.cancel();
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        this.finish();
    }

    private void createVideoFolder(){
        File movieFile = this.getExternalFilesDir(null);
        mVideoFolder = new File(movieFile,"GestureVideo");
        if(!mVideoFolder.exists()) mVideoFolder.mkdir();
    }

    private File createVideoFileName() throws IOException {

        File videoFile;
        int i = 1;
        do {
            videoFile = new File("/storage/emulated/0/Android/data/com.example.smarthomegesturecontrol/files/GestureVideo/" + item + "_PRACTICE_" + i + "_MUNEKAGE.mp4");
            i++;
        } while (videoFile.exists());
        mVideoFileName = videoFile.getAbsolutePath();
        return videoFile;
    }

    private void setupMediaRecorder() throws IOException {
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat((MediaRecorder.OutputFormat.MPEG_4));
        mMediaRecorder.setOutputFile(mVideoFileName);
        mMediaRecorder.setVideoEncodingBitRate(1000000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(1080,1920);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setOrientationHint(90);
        mMediaRecorder.prepare();
    }
}
