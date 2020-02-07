package com.dryht.mobile.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.ImageWriter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dryht.mobile.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import org.opencv.imgcodecs.Imgcodecs.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FdActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    protected static int LIMIT_TIME = 3;//临界的时间，超过3秒保存图片一次
    protected static Bitmap mFaceBitmap;//包含有人脸的图像
    private CameraBridgeViewBase openCvCameraView;

    private static final String TAG = "数据结果：";

    private Handler mHandler;

    private CascadeClassifier mFrontalFaceClassifier = null; //正脸 级联分类器
    private CascadeClassifier mProfileFaceClassifier = null; //侧脸 级联分类器

    //存储人脸
    private Map<String,Bitmap> bitmaps;
    //文件名/文件
    private ArrayList<String> filename = new ArrayList<>();
    private ArrayList<String> file = new ArrayList<>();
    //显示是录制还是识别
    private int limit = 0;
    private Rect[] mFrontalFacesArray;
    private Rect[] mProfileFacesArray;
    //保留正脸数据用于分析性别和年龄
    private Rect[] mTempFrontalFacesArray;

    private Mat mRgba; //图像容器
    private Mat mGray;
    private TextView mFrontalFaceNumber;
    private TextView mProfileFaceNumber;
    private TextView mCurrentNumber;
    private TextView mWaitTime;
    private ImageView imageView;

    private int mFrontFaces = 0;
    private int mProfileFaces = 0;


    //记录的时间，秒级别
    private long mRecodeTime;
    //记录的时间，毫秒级别，空闲时间，用来计数，实现0.5秒一次检查
    private long mRecodeFreeTime;
    //当前的时间，秒级别
    private long mCurrentTime = 0;
    //当前的时间，毫秒级别
    private long mMilliCurrentTime = 0;
    //观看时间
    private int mWatchTheTime = 0;


    //设置检测区域
    private Size m55Size = new Size(55, 55);
    private Size m65Size = new Size(65, 65);
    private Size mDefault = new Size();
    //解决横屏问题
    private Mat Matlin;
    private Mat gMatlin;
    private int absoluteFaceSize;
    //人脸矩形框
    Rect facerect = null;
    //人脸垫子
    Mat faceimage = null;
    private String baseUrl=null;
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;

    //图片保存路径
    private String picPath = "/storage/emulated/0/DCIM/Camera/";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_detect_surface_view);
        mHandler = new Handler();
        //获取flag判断是录入还是识别
        limit =  getIntent().getIntExtra("flag",1);
        if(limit>1)
            baseUrl = "recordfacedata/";
        else
            baseUrl = "facelogin/";
        checkPermission();
        //初始化控件
        initComponent();
        //初始化摄像头
        initCamera();
        //opencv资源的初始化在父类的 onResume 方法中
        //去寻找是否已经有了相机的权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
        }else {
            //否则去请求相机权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "OpenCV init error");
        }
        //初始化opencv资源
        initOpencv();
    }

    /**
     * @Description 初始化组件
     */
    protected void initComponent() {
        openCvCameraView = findViewById(R.id.javaCameraView);
        mFrontalFaceNumber = findViewById(R.id.tv_frontal_face_number);
        mProfileFaceNumber = findViewById(R.id.tv_profile_face_number);
        mCurrentNumber = findViewById(R.id.tv_current_number);
        mWaitTime = findViewById(R.id.tv_wait_time);
        imageView = findViewById(R.id.image1);
    }

    /**
     * @Description 初始化摄像头
     */
    protected void initCamera() {
        int camerId = 1;
        Camera.CameraInfo info = new Camera.CameraInfo();
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, info);
            Log.v("yaoxumin", "在 CameraRenderer 类的 openCamera 方法 中执行了开启摄像 Camera.open 方法,cameraId:" + i);
            camerId = i;
            break;
        }
        openCvCameraView.setCameraIndex(1); //摄像头索引        -1/0：后置双摄     1：前置
        openCvCameraView.enableFpsMeter(); //显示FPS
        openCvCameraView.setCvCameraViewListener(this);//监听
        openCvCameraView.setMaxFrameSize(950, 500);//设置帧大小
    }

    /**
     * @Description 初始化opencv资源
     */
    protected void initOpencv() {
        initFrontalFace();
        initProfileFace();
        // 显示
        openCvCameraView.enableView();
    }

    /**
     * @Description 初始化正脸分类器
     */
    public void initFrontalFace() {
        try {
            //这个模型是我觉得来说相对不错的
            InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt); //OpenCV的人脸模型文件： lbpcascade_frontalface_improved
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            // 加载 正脸分类器
            mFrontalFaceClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * @Description 初始化侧脸分类器
     */
    public void initProfileFace() {
        try {
            //这个模型是我觉得来说相对不错的
            InputStream is = getResources().openRawResource(R.raw.haarcascade_profileface); //OpenCV的人脸模型文件： lbpcascade_frontalface_improved
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "haarcascade_profileface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            // 加载 侧脸分类器
            mProfileFaceClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
        mGray = new Mat();
        //解决横屏问题
        Matlin = new Mat(width, height, CvType.CV_8UC3);
        gMatlin = new Mat(width, height, CvType.CV_8UC3);

        absoluteFaceSize = (int)(height * 0.6);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        gMatlin.release();
        Matlin.release();
    }

    /**
     * @Description 在这里实现人脸检测和性别年龄识别
     */
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        mRgba = inputFrame.rgba(); //RGBA
//        mGray = inputFrame.gray(); //单通道灰度图
//        //解决  前置摄像头旋转显示问题
//        Core.flip(mRgba, mRgba, 1); //旋转,变成镜像
//
//        mMilliCurrentTime = System.currentTimeMillis() / 100;//获取当前时间毫秒级别
//        mCurrentTime = mMilliCurrentTime / 10;//获取当前时间，秒级别
//
//        //每0.5秒检查一次，如果你一直开测，不做这个过滤，那么大概一秒可能是起码5次计算检测是否有人脸，这样的计算对于机器有一定的压力，
//        //而且这些计算起始可以不用那么多的，我个人觉得合适的是，
//        //没有检测到人的时候，1秒2次检测
//        //检测到人的时候，修改为1秒1次检测，这样你开一天甚至几天也不会出现过热导致app退出
//        if (mRecodeFreeTime + 5 <= mMilliCurrentTime) {
//            mRecodeFreeTime = mMilliCurrentTime;
//            if (mRecodeTime == 0 || mCurrentFaceSize == 0 || mRecodeTime < mCurrentTime) {//识别到人之后，1秒做一次检测
//                mRecodeTime = mCurrentTime;//记录当前时间
//                //检测并显示
//                MatOfRect frontalFaces = new MatOfRect();
//
//                if (mFrontalFaceClassifier != null) {//这里2个 Size 是用于检测人脸的，越小，检测距离越远，1.1, 5, 2, m65Size, mDefault着四个参数可以提高检测的准确率，5表示确认五次，具体百度 detectMultiScale 这个方法
//                    mFrontalFaceClassifier.detectMultiScale(mGray, frontalFaces, 1.1, 5, 2, m65Size, mDefault);
//                    mFrontalFacesArray = frontalFaces.toArray();
//                    if (mFrontalFacesArray.length > 0) {
//                        Log.i(TAG, "正脸人数为 : " + mFrontalFacesArray.length);
//                    }
//                    mCurrentFaceSize = mFrontFaces = mFrontalFacesArray.length;
//                }
//            }
                   /* Bitmap bitmap = Bitmap.createBitmap(mRgba.width(), mRgba.height(), Bitmap.Config.RGB_565);
                    Utils.matToBitmap(mRgba, bitmap);
                    mFaceBitmap = bitmap;*/
/////////////////////////
        mMilliCurrentTime = System.currentTimeMillis() / 100;//获取当前时间毫秒级别
        mCurrentTime = mMilliCurrentTime / 10;//获取当前时间，秒级别
        int rotation = openCvCameraView.getDisplay().getRotation();

        //使前置的图像也是正的
        mRgba = inputFrame.rgba(); //RGBA
        mGray = inputFrame.gray(); //单通道灰度图
        //解决  前置摄像头旋转显示问题
        Core.flip(mRgba, mRgba, 1); //旋转,变成镜像
        Core.flip(mGray, mGray, 1);


        //MatOfRect faces = new MatOfRect();
        if (mRecodeFreeTime + 10 <= mMilliCurrentTime) {
            mRecodeFreeTime = mMilliCurrentTime;
            if (mRecodeTime == 0 || mRecodeTime < mCurrentTime) {//识别到人之后，1秒做一次检测
                mRecodeTime = mCurrentTime;//记录当前时间
                if (rotation == Surface.ROTATION_0) {
                    MatOfRect faces = new MatOfRect();
                    Core.rotate(mGray, gMatlin, Core.ROTATE_90_CLOCKWISE);
                    Core.rotate(mRgba, Matlin, Core.ROTATE_90_CLOCKWISE);
                    if (mFrontalFaceClassifier != null) {
                        mFrontalFaceClassifier.detectMultiScale(gMatlin, faces, 1.1, 5, 2, new Size(absoluteFaceSize, absoluteFaceSize), new Size());
                    }
                    Rect[] faceArray = faces.toArray();

                    for (int i = 0; i < faceArray.length; i++) {
                        Imgproc.rectangle(Matlin, faceArray[i].tl(), faceArray[i].br(), new Scalar(255, 255, 255), 2);
                        if (i == faceArray.length - 1) {
                            facerect = new Rect(faceArray[i].x, faceArray[i].y, faceArray[i].width, faceArray[i].height);
                        }
                    }
                    Core.rotate(Matlin, mRgba, Core.ROTATE_90_COUNTERCLOCKWISE);

                }
                if(null != facerect)
                {
                    faceimage = new Mat(Matlin,facerect);
                    //把mat转换成bitmap
                    saveImg(faceimage);
                    if (file.size()>limit)
                    {
                        //挂起
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                openCvCameraView.disableView();
                            }
                        }, 0);
                        OkHttpClient mOkHttpClient=new OkHttpClient();
                        MultipartBody.Builder builder=  new MultipartBody.Builder().setType(MultipartBody.FORM);
                            for (int i = 0;i<file.size();i++) {
                                System.out.println("***********************************************");
                                System.out.println(file.get(i));
                                System.out.println(String.valueOf(file.get(i)));
                                System.out.println("***********************************************");
                                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), new File(String.valueOf(file.get(i))));
                                builder.addFormDataPart("img",filename.get(i),fileBody);
                            }
                        Request mRequest=new Request.Builder()
                                .url(com.dryht.mobile.Util.Utils.generalUrl+baseUrl)
                                .post(builder.build())
                                .build();

                        mOkHttpClient.newCall(mRequest).enqueue(new Callback(){
                            @Override
                            public void onResponse(@NotNull okhttp3.Call call, @NotNull Response response) throws IOException {
                                Looper.prepare();
                                //String转JSONObject
                                JSONObject result = null;
                                try {
                                    result = new JSONObject(response.body().string());
                                    System.out.println(result.get("status"));
                                    //取数据
                                    if(result.get("status").equals("1"))
                                    {
                                        file=null;
                                        filename=null;
                                        Intent intent=new Intent();
                                        //跳转到指定的Activity
                                        intent.setClass(FdActivity.this, LoginActivity.class);
                                        //启动Activity
                                        startActivity(intent);
                                        onDestroy();
//                                        //成功结果主页
//                                        mHandler.postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                Intent intent=new Intent();
//                                                //跳转到指定的Activity
//                                                intent.setClass(FdActivity.this, LoginActivity.class);
//                                                //启动Activity
//                                                startActivity(intent);
//
//                                            }
//                                        }, 0);

                                    }
                                    else
                                    {
                                        Toast.makeText(FdActivity.this,String.valueOf(result.get("result")),Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent();
                                        //跳转到指定的Activity
                                        intent.setClass(FdActivity.this, LoginActivity.class);
                                        //启动Activity
                                        startActivity(intent);

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Looper.loop();

                            }

                            @Override
                            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                                Looper.prepare();
                                Toast.makeText(FdActivity.this,"登录失败",Toast.LENGTH_LONG).show();
                                Looper.loop();
                            }
                        });
                    }

                    }
                }
            }

        return mRgba;
    }



    private void saveImg(Mat rgba) {
        //先把mat转成bitmap
        Bitmap mBitmap = null;
        //Imgproc.cvtColor(seedsImage, rgba, Imgproc.COLOR_GRAY2RGBA, 4); //转换通道
        mBitmap = Bitmap.createBitmap(rgba.cols(), rgba.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(rgba, mBitmap);
        FileOutputStream fileOutputStream = null;
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/smartass/");
        if(!f.exists()){
            //创建文件夹
            f.mkdirs();
        }
        int q=(int)(Math.random()*100);
        try {
            File img = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/smartass/"+q+".jpeg");
            if(!img.exists()){
                //创建文件夹
                img.createNewFile();
            }
            fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/smartass/"+q+".jpeg");
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            filename.add(q+".jpeg");
            file.add(Environment.getExternalStorageDirectory().getAbsolutePath()+"/smartass/"+q+".jpeg");
            //回收
            fileOutputStream.flush();
            fileOutputStream.close();
            mBitmap.recycle();
            Log.d(TAG, "图片已保存至本地");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void checkPermission() {

        if (Build.VERSION.SDK_INT >= 23) {
            int write = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (write != PackageManager.PERMISSION_GRANTED || read != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 300);
            } else {
                String name = "CrashDirectory";
                File file1 = new File(Environment.getExternalStorageDirectory(), name);
                if (file1.mkdirs()) {
                    Log.i("wytings", "permission -------------> " + file1.getAbsolutePath());
                } else {
                    Log.i("wytings", "permission -------------fail to make file ");
                }
            }
        } else {
            Log.i("wytings", "------------- Build.VERSION.SDK_INT < 23 ------------");
        }
    }


}

