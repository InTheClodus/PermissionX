# PermissionX
Android 权限申请封装，Java版本
使用案例
```java
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
```
