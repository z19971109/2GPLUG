//package smart2g.dyx.com.a2gplug;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.FeatureInfo;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.PixelFormat;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.FragmentActivity;
//import android.support.v7.widget.AppCompatImageView;
//import android.support.v7.widget.LinearLayoutCompat;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.LinearLayout;
//import android.widget.Toast;
//
//import com.uuzuche.lib_zxing.activity.CaptureFragment;
//import com.uuzuche.lib_zxing.activity.CodeUtils;
//
//
///**
// * 定制化显示扫描界面
// */
//public class SecondActivity extends FragmentActivity {
//
//    private CaptureFragment captureFragment;
//
//    private LinearLayoutCompat flashLightLayout;
//    private AppCompatImageView flashLightIv;
//
//    private LinearLayoutCompat albumLayout;
//
//    private AppCompatImageView bacIV;
//
//    public static final int REQUEST_IMAGE = 112;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_second);
//        setTitleHeight();
//        captureFragment = new CaptureFragment();
//        // 为二维码扫描界面设置定制化界面
//        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera);
//        captureFragment.setAnalyzeCallback(analyzeCallback);
//        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();
//
////        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
////        surfaceView.setZOrderMediaOverlay(true);
////        surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
//
//        initView();
//
//        initPermission();
//    }
//
//    private void setTitleHeight (){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
////            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
//
//        int statusBarHeight1 = -1;
//        //获取status_bar_height资源的ID
//        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            //根据资源ID获取响应的尺寸值
//            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
//        }
//        View view = findViewById(R.id.second_title);
//        LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) view.getLayoutParams();
//        params.height = statusBarHeight1;
//        view.setLayoutParams(params);
//    }
//
//
//    private void initPermission() {
//        //检查权限
//        String[] permissions = CheckPermissionUtils.checkPermission(this);
//        if (permissions.length == 0) {
//            //权限都申请了
//            //是否登录
//        } else {
//            //申请权限
//            ActivityCompat.requestPermissions(this, permissions, 100);
//        }
//    }
//
//    public static boolean isSupportCameraLedFlash(PackageManager pm) {
//        if (pm != null) {
//            FeatureInfo[] features = pm.getSystemAvailableFeatures();
//            if (features != null) {
//                for (FeatureInfo f : features) {
//                    if (f != null && PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
//
//    public static boolean isOpen = false;
//
//    private void initView() {
//        bacIV = (AppCompatImageView) findViewById(R.id.backIv);
//        flashLightLayout = (LinearLayoutCompat) findViewById(R.id.flashLightLayout);
//        albumLayout = (LinearLayoutCompat) findViewById(R.id.albumLayout);
//        flashLightIv = (AppCompatImageView) findViewById(R.id.flashLightIv);
//        bacIV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        flashLightLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isOpen) {
//                    flashLightIv.setImageResource(R.drawable.ic_open);
//                    CodeUtils.isLightEnable(true);
//                    isOpen = true;
//                } else {
//                    flashLightIv.setImageResource(R.drawable.ic_close);
//                    CodeUtils.isLightEnable(false);
//                    isOpen = false;
//                }
//
//            }
//        });
//        albumLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                startActivityForResult(intent, REQUEST_IMAGE);
//            }
//        });
//
//
//        if (isSupportCameraLedFlash(getPackageManager())) {
//            flashLightLayout.setVisibility(View.VISIBLE);
//        } else {
//            flashLightLayout.setVisibility(View.GONE);
//        }
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_IMAGE) {
//            if (data != null) {
//                Uri uri = data.getData();
//                try {
//                    CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(this, uri), new CodeUtils.AnalyzeCallback() {
//                        @Override
//                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
//                            Toast.makeText(SecondActivity.this, "解析结果:" + result, Toast.LENGTH_LONG).show();
//                            finish();
//                        }
//
//                        @Override
//                        public void onAnalyzeFailed() {
//                            Toast.makeText(SecondActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
//                        }
//                    });
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//    }
//
//    /**
//     * 二维码解析回调函数
//     */
//    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
//        @Override
//        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
//            Intent resultIntent = new Intent();
//            Bundle bundle = new Bundle();
//            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
//            bundle.putString(CodeUtils.RESULT_STRING, result);
//            resultIntent.putExtras(bundle);
//            SecondActivity.this.setResult(RESULT_OK, resultIntent);
//            SecondActivity.this.finish();
//        }
//
//        @Override
//        public void onAnalyzeFailed() {
//            Intent resultIntent = new Intent();
//            Bundle bundle = new Bundle();
//            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
//            bundle.putString(CodeUtils.RESULT_STRING, "");
//            resultIntent.putExtras(bundle);
//            SecondActivity.this.setResult(RESULT_OK, resultIntent);
//            SecondActivity.this.finish();
//        }
//    };
//}
