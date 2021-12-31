package com.nf.permissionx;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** * 权限申请 * Created by lidong on 2021/12/31. */
public class PermissionActivity extends AppCompatActivity {

    private static PermissionHelper.PermissionListener mPermissionListener;

    private String[] permissions;
    private AlertDialog mAlertDialog;
    private boolean first = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissions = getIntent().getStringArrayExtra("data");
    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(this, permissions, 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (first) {
            first = false;
            requestPermissions();
            return;
        }

        //切换页面回来从新校验一下
        showMissingPermissionDefiniteDialog();
    }

    /** * 拒绝受权 显示提示对话框 */
    private void showMissingPermissionDefiniteDialog() {

        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }

        List<String> deniedList = new ArrayList<>();
        for (String permission : permissions) {
            if (PermissionChecker.checkSelfPermission(this, permission)
                    == PermissionChecker.PERMISSION_GRANTED) {
                continue;
            }
            deniedList.add(permission);
        }

        if (deniedList.size() == 0) {
            granted();
            return;
        }

        List<String> tipList = transformTip(deniedList);
        StringBuilder tip = new StringBuilder();
        int i = 0;
        for (String temp : tipList) {
            if (i > 0) {
                tip.append(",");
            }
            tip.append(temp);
            i++;
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(PermissionActivity.this);
        builder.setMessage("应用程序须要如下权限：\n\r" + tip);

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                denied();
            }
        });
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gotoSetting();
            }
        });
        builder.setCancelable(false);
        mAlertDialog = builder.show();
    }

    private List<String> transformTip(List<String> list) {
        Map<String, String> permissionList = new HashMap<>();
        permissionList.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "「存储空间」");
        permissionList.put(Manifest.permission.READ_PHONE_STATE, "「电话」");
        permissionList.put(Manifest.permission.CAMERA, "「相机」");
        permissionList.put(Manifest.permission.RECORD_AUDIO, "「录音」");
        permissionList.put(Manifest.permission.ACCESS_FINE_LOCATION, "「位置」");
        permissionList.put(Manifest.permission.ACCESS_COARSE_LOCATION, "「位置」");

        List<String> tipList = new ArrayList<>();
        for (String temp : list) {
            String tip = permissionList.get(temp);
            if (!tipList.contains(tip)) {
                tipList.add(tip);
            }
        }

        return tipList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //判断全部权限是否被受权
        if (isGranted(grantResults) && PermissionHelper.checkPermission(this, permissions)) {
            granted();
        } else {
            showMissingPermissionDefiniteDialog();
        }
    }

    /** * 受权回调 */
    private void granted(){
        if (mPermissionListener != null) {
            mPermissionListener.granted();
        }
        finish();
    }

    /** * 拒绝回调 */
    private void denied(){
        if (mPermissionListener != null) {
            mPermissionListener.denied();
        }
        finish();
    }

    private boolean isGranted(int[] grantResult) {
        for (int result : grantResult) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /** * 跳转到当前应用对应的设置页面 */
    private void gotoSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
        mPermissionListener = null;
    }

    public static void open(Context context, String[] permissions, PermissionHelper.PermissionListener listener) {
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.putExtra("data", permissions);
        context.startActivity(intent);
        mPermissionListener = listener;
    }
}
