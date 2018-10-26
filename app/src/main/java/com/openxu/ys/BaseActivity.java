package com.openxu.ys;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.openxu.oxlib.utils.LogUtil;
import com.openxu.oxlib.utils.PermissionUtils;
import com.openxu.oxlib.view.TitleLayout;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {
    protected String TAG;

    protected Context mContext;
    protected TitleLayout title_layout;
    /**
     * 需要申请的权限
     */
    static final String[] PERMISSION = new String[]{
            Manifest.permission.RECORD_AUDIO, //录音
            Manifest.permission.READ_PHONE_STATE,  //读取手机信息权限
//            Manifest.permission.READ_CONTACTS,  //读取联系人权限
            Manifest.permission.WRITE_EXTERNAL_STORAGE, // 写入权限
            Manifest.permission.READ_EXTERNAL_STORAGE,  //读取权限
//            Manifest.permission.WRITE_SETTINGS,  //配置权限
//            Manifest.permission.ACCESS_FINE_LOCATION,  //定位
//            Manifest.permission.CAMERA //人脸识别 摄像头
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
        mContext = this;
        int layoutID = getLayoutID();
        if (layoutID != 0) {
            setContentView(layoutID);
           // ButterKnife.bind(this);
            View title = findViewById(R.id.title_layout);
            if (title != null) {
                title_layout = (TitleLayout) title;
                //默认处理返回键
                title_layout.setOnMenuClickListener((menu, view) -> {
                    switch (menu) {
                        case MENU_BACK:
                            LogUtil.e(TAG, "返回--------------------");
                            onBackPressed();
                            return;
                    }
                    onMenuClick(menu, view);
                });
            }
        }

        initView();
        setListener();
        init_Data();

    }

    protected abstract int getLayoutID();

    protected void initView() { }

    protected void setListener() { }

    protected void onMenuClick(TitleLayout.MENU_NAME menu, View view) { }

    protected void init_Data() {}


    /************************权限相关**************************/
    @Override
    protected void onStart() {
        super.onStart();
        checkCameraPermission();
    }

    /**
     * 6.0权限检测
     */
    private void checkCameraPermission() {
//        LogUtil.i(TAG, "======================申请系列必要权限========================");
        if (PermissionUtils.checkPermissionArray(this, PERMISSION, PermissionUtils.PERMISSION_ARRAY)) {
            //如果所需权限全部被允许，执行下面方法
            LogUtil.i(TAG, "系列必要权限全部通过");
            getAllGrantedPermission();
        } else {
            LogUtil.e(TAG, "系列必要权限有部分未通过");
        }
    }
    /**
     * 当获取到所需权限后，进行相关业务操作
     */
    public void getAllGrantedPermission() {
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.PERMISSION_ARRAY:
                List<String> list = new ArrayList<>();
                if (PermissionUtils.verifyPermissions(grantResults, permissions, list)) {
                    LogUtil.d(TAG, "权限组被允许");
                    getAllGrantedPermission();
                } else {
                    String pers = PermissionUtils.getUnRrantName(list);
                    LogUtil.e(TAG, pers + "权限被拒绝了");
                    // Permission Denied
                    String msg = "当前应用缺少" + (list.size() > 1 ? (list.size() + "项") : " ") + "必要权限。\n详情如下：\n"
                            + pers +
                            "请点击\"设置\"-\"权限\"-打开所需权限。\n" +
                            "返回后退出应用并重新登录。";
                    new AlertDialog.Builder(this)
                            .setTitle("权限申请")
                            .setMessage(msg)
                            .setPositiveButton("去设置", (DialogInterface dialogInterface, int i) -> {
                                PermissionUtils.showInstalledAppDetails(this, getPackageName());
                            })
                            .setNegativeButton("取消", (DialogInterface dialogInterface, int i) -> {
                                //退出
                                finish();
                            }).setCancelable(false)
                            .show();
                }
                break;
        }
    }

    /************************权限相关**************************/


}
