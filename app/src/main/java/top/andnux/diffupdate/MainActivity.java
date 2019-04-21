package top.andnux.diffupdate;


import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x00);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ApkInstallUtil.onActivityResult(this, requestCode, resultCode, data);
    }

    public void download(View view) {
        DownLoadTask task = new DownLoadTask(new DownLoadTask.DownLoadTaskListener() {
            @Override
            public void onPreExecute() {
                Log.d(TAG, "onPreExecute() called");
            }

            @Override
            public void onProgressUpdate(int current, int total) {
                Log.d(TAG, "onProgressUpdate() called with: current = [" + current + "], total = [" + total + "]");
            }

            @Override
            public void onPostExecute(File file) {
                Log.d(TAG, "onPostExecute() called with: file = [" + file.getAbsolutePath() + "]");
                ApkInstallUtil.installPatch(MainActivity.this, file);
            }
        });
        task.execute("http://192.168.31.11:8080/v1_v2.patch");
    }
}
