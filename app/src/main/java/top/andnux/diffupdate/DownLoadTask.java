package top.andnux.diffupdate;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class DownLoadTask extends AsyncTask<String, Integer, File> {

    private DownLoadTaskListener mListener;

    public DownLoadTask() {

    }

    public DownLoadTask(DownLoadTaskListener listener) {
        mListener = listener;
    }

    interface DownLoadTaskListener {

        void onPreExecute();

        void onProgressUpdate(int current, int total);

        void onPostExecute(File file);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mListener != null) {
            mListener.onPreExecute();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (mListener != null) {
            mListener.onProgressUpdate(values[0], values[1]);
        }
    }

    @Override
    protected File doInBackground(String... strings) {
        HttpURLConnection conn = null;
        InputStream is = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            if (strings.length > 0) {
                String string = strings[0];
                URL url = new URL(string);
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                is = conn.getInputStream();
                int length = conn.getContentLength();
                File directory = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS);
                file = new File(directory,
                        string.substring(string.lastIndexOf("/") + 1));
                fos = new FileOutputStream(file);
                byte[] bytes = new byte[4 * 1024];
                int len = 0;
                int count = 0;
                while ((len = is.read(bytes)) != -1) {
                    count += len;
                    publishProgress(count, length);
                    fos.write(bytes, 0, len);
                }
                fos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        if (mListener != null) {
            mListener.onPostExecute(file);
        }
    }
}
