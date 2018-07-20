package android.com.myapplication;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private SurfaceView sv_camera_sufaceview;
    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,

                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.activity_main);
        wakeUpAndUnlock();
        sv_camera_sufaceview = findViewById(R.id.sv_camera_surfaceview);
        sv_camera_sufaceview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                camera = Camera.open();
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPictureFormat(PixelFormat.JPEG);
                parameters.set("jpeg-quality",100);
                camera.setParameters(parameters);
                try{
                    camera.setPreviewDisplay(sv_camera_sufaceview.getHolder());
                    camera.startPreview();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
        mHandler.postDelayed(mRunnable,8000);
    }

    private int count = 0;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        public void run() {
            Log.e("xuzhenyue", Thread.currentThread().getName() + " " + count);
            count++;
            setTitle("" + count);
            takePhoto();
            // 每8秒执行一次
            mHandler.postDelayed(mRunnable, 8000);  //给自己发送消息，自运行
        }
    };
    public void takePhoto(){
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                try{
                    long curTime = System.currentTimeMillis();
                    //xzy modify for overlay record filename
                    Log.d("xuzhenyue","123456");
                    Date date = new Date(curTime);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_dd_MM_HH_mm_ss",
                            Locale.ENGLISH);
                    String time = simpleDateFormat.format(date);
                    FileOutputStream fileOutputStream = new FileOutputStream("/storage/emulated/legacy/DCIM/Camera/" + time+".jpg");
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                    camera.stopPreview();
                    camera.startPreview();
                }catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void takePhoto1(View view){
        mHandler.removeCallbacks(mRunnable);
    }
    public void wakeUpAndUnlock() {
        // 获取电源管理器对象
        PowerManager pm = (PowerManager) this
                .getSystemService(Context.POWER_SERVICE);
        boolean screenOn = pm.isScreenOn();
        if (!screenOn) {
            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire(10000); // 点亮屏幕
            wl.release(); // 释放
        }
        // 屏幕解锁
        KeyguardManager keyguardManager = (KeyguardManager) this
                .getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("unLock");
        // 屏幕锁定
        keyguardLock.reenableKeyguard();
        keyguardLock.disableKeyguard(); // 解锁
    }
}
