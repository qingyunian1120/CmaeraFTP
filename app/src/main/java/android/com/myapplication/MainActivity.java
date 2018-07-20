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

import java.io.File;
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
        wakeUpAndUnlock();//亮屏并解锁
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
        //删除文件格式
        //deleteFile("/storage/emulated/legacy/DCIM/Camera/" + "2018_07_20_15_31_21.jpg");
        mHandler.postDelayed(mRunnable,8000);
    }

    private int count = 0;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        public void run() {
            Log.e("xuzhenyue", Thread.currentThread().getName() + " " + count);
            count++;
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
                    long curTime = System.currentTimeMillis();//获取当前时间
                    //xzy modify for overlay record filename
                    Log.d("xuzhenyue","123456");
                    Date date = new Date(curTime);//转换为年月日的时间
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss",
                            Locale.ENGLISH);//设置时间格式
                    String time = simpleDateFormat.format(date);//将时间转换为设置的时间格式
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

    /**
     * 删除单个文件
     *
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public  boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.d("xuzhenyue","删除单个文件" + fileName + "成功！");
                return true;
            } else {
                Log.d("xuzhenyue","删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            Log.d("xuzhenyue","删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

}
