package com.example.mycamera;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import java.io.IOException;

public class CameraManager {
    private static final String TAG = "CameraManager";
    private static final int MSG_OPEN = 1;
    private static final int MSG_STARTPREVIEW = 2;
    private static final int MSG_STOPPREVIEW = 3;
    private static final int MSG_RELEASE = 4;
    private Context mContext;
    private Handler mCameraHandler;
    private SurfaceHolder mSurfaceHolder;
    private int mCameraID;
    private Camera mCamera;
    private boolean mbCameraReady=false;

    public CameraManager(Context context) {
        mContext = context;
        HandlerThread cameraHandlerThread = new HandlerThread("CameraHandler");
        cameraHandlerThread.start();
        mCameraHandler = new Handler(cameraHandlerThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case MSG_OPEN:
                        openCamera(mCameraID);
                        break;
                    case MSG_STARTPREVIEW:
                        mCamera.startPreview();
                        mbCameraReady=true;
                        break;
                    case MSG_STOPPREVIEW:
                        mCamera.stopPreview();
                        break;
                    case MSG_RELEASE:
                        mCamera.release();
                        mCamera=null;
                        break;
                }
            }
        };
    }

    public void init() {
        mCameraID = 0;
    }

    public void openCamera() {
        mCameraHandler.sendEmptyMessage(MSG_OPEN);
    }

    public void openCamera(int id) {
        Log.i(TAG, "openCamera: " + id);
        try {
            mCamera = Camera.open(id);
            Camera.Parameters parameters = mCamera.getParameters();
            mCamera.setParameters(parameters);
            if (mSurfaceHolder != null) {
                mCamera.setPreviewDisplay(mSurfaceHolder);
            }
            mCamera.setDisplayOrientation(90);
            mCameraHandler.sendEmptyMessage(MSG_STARTPREVIEW);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "openCamera: 相机打开异常");
        }
    }


    public void setSurfaceHolder(SurfaceHolder holder) {
        if (holder != null) {
            mSurfaceHolder = holder;
            if (mCamera!=null)
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCameraHandler.sendEmptyMessage(MSG_STARTPREVIEW);
            } catch (IOException e) {
                e.printStackTrace();

            }

        }
    }

    public void stopPreview() {
        Log.i(TAG, "stopPreview: ");
        mCameraHandler.sendEmptyMessage(MSG_STOPPREVIEW);
    }

    public void release() {
        Log.i(TAG, "release: ");
        mCameraHandler.sendEmptyMessage(MSG_RELEASE);
    }
    public void switchCamera() {
        Log.i(TAG, "switchCamera: ");
        if (!mbCameraReady)return;
        mbCameraReady=false;
        mCameraID=mCameraID==0?1:0;
        mCameraHandler.sendEmptyMessage(MSG_STOPPREVIEW);
        mCameraHandler.sendEmptyMessage(MSG_RELEASE);
        openCamera();
    }
}