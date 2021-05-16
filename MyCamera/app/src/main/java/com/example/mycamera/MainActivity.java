package com.example.mycamera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUESTCODE_CAMERA=1;
    private SurfaceHolder mSurfaceHolder;
    private CameraManager mCameraManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.cameraswitch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraManager.switchCamera();
            }
        });
        SurfaceView surfaceView=findViewById(R.id.surfaceView);
        mSurfaceHolder=surfaceView.getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback2() {
            @Override
            public void surfaceRedrawNeeded(@NonNull SurfaceHolder holder) {

            }

            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                Log.i(TAG, "surfaceCreated: ");
                mCameraManager.setSurfaceHolder(holder);
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                Log.i(TAG, "surfaceDestroyed: ");

            }
        });
        mCameraManager=new CameraManager(getApplicationContext());
        mCameraManager.init();
        if (PackageManager.PERMISSION_DENIED== checkCallingOrSelfPermission(Manifest.permission.CAMERA)){
            requestPermissions(new String[]{Manifest.permission.CAMERA},REQUESTCODE_CAMERA);
        }else {
            mCameraManager.openCamera();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {
       if (requestCode == REQUESTCODE_CAMERA){
           if (checkCallingOrSelfPermission(Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
               mCameraManager.openCamera();
           }else{
               Toast.makeText(getApplicationContext(),"没有授权使用相机!",Toast.LENGTH_LONG).show();
           }

       }
    }

    @Override
    protected void onStop() {
        mCameraManager.stopPreview();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mCameraManager.release();
        super.onDestroy();
    }
}