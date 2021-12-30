package com.nf.permissionx;

import android.content.Context;
import android.os.Build;

import androidx.core.content.PermissionChecker;

/** * 权限帮助类 * Created by lidong on 2019/10/25. */
public class PermissionHelper {

    /** * 请求权限 * * @param context context * @param permissions 权限 * @param listener 监听 */
    public static void request(Context context, String[] permissions, PermissionListener listener) {
        //判断权限，若是全部权限全被受权，直接返回
        if (checkPermission(context, permissions)) {
            if (listener != null) {
                listener.granted();
            }
            return;
        }

        //申请权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {  //6.0如下没有申请权限，直接返回拒绝
            if (listener != null) {
                listener.denied();
            }
        } else {  //6.0及以上申请权限
            //为了统一回调须要借用一个activity
            PermissionActivity.open(context, permissions, listener);
        }
    }

    /** * 校验权限 * * @param context context * @param permissions 权限 * @return 全部权限已被受权返回true，不然返回false */
    protected static boolean checkPermission(Context context, String[] permissions) {
        for (String per : permissions) {
            int result = PermissionChecker.checkSelfPermission(context, per);
            if (result != PermissionChecker.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /** * 权限监听 */
    public interface PermissionListener {
        /** * 全部权限已被受权 */
        void granted();

        /** * 一个或多个权限被拒绝 */
        void denied();
    }
}
