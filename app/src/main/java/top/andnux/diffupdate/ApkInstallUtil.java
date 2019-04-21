package top.andnux.diffupdate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import java.io.File;

public class ApkInstallUtil {

    private static final String TAG = "ApkInstallUtil";
    private static final int REQUEST_CODE_APP_INSTALL = 0x999;
    private static String sFile = null;

    public static void onActivityResult(Activity activity, int requestCode,
                                        int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult() called with: activity = [" + activity + "], requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (requestCode == REQUEST_CODE_APP_INSTALL) {
            if (!TextUtils.isEmpty(sFile)) {
                doInstall(activity, new File(sFile));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void startInstallPermissionSettingActivity(Activity activity) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        activity.startActivityForResult(intent, REQUEST_CODE_APP_INSTALL);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static boolean isHasInstallPermissionWithO(Context context) {
        if (context == null) {
            return false;
        }
        return context.getPackageManager().canRequestPackageInstalls();
    }


    public static void installPatch(final Activity activity, final File patchFile) {
        final String path = activity.getPackageResourcePath();
        new Thread() {
            @Override
            public void run() {
                super.run();
                File directory = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS);
                final File file = new File(directory, System.currentTimeMillis() + ".apk");
                Bspatch.bspatch(path, file.getAbsolutePath(), patchFile.getAbsolutePath());
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        install(activity, file);
                    }
                });
            }
        }.start();
    }

    public static void install(Activity activity, File file) {
        sFile = file.getAbsolutePath();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean hasInstallPermission = isHasInstallPermissionWithO(activity);
            if (!hasInstallPermission) {
                startInstallPermissionSettingActivity(activity);
                return;
            }
        }
        doInstall(activity, file);
    }

    private static void doInstall(Activity activity, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
            Uri apkUri = FileProvider.getUriForFile(activity, activity.getPackageName()
                    + ".provider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri,
                    "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
        }
        activity.startActivity(intent);
    }
}
