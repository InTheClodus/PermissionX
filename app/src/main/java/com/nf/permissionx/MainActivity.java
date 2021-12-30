package com.nf.permissionx;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int CALL_PHONE_REQUEST_CODE = 0x0001;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btn);
        btn.setOnClickListener(view -> requestCallPhone());
    }

    private void requestCallPhone() {
        String[] permissions=new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE,
        };
        PermissionHelper.request(this, permissions, new PermissionHelper.PermissionListener() {
            @Override
            public void granted() {
                Log.i("TAG","已同意");
            }

            @Override
            public void denied() {
                Log.i("TAG","已拒绝");
            }
        });
    }
}
