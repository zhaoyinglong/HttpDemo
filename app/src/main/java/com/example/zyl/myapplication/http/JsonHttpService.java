package com.example.zyl.myapplication.http;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zhaoyinglong on 2017/11/7.
 */

public class JsonHttpService implements IHttpService {

    private final String TAG = JsonHttpService.class.getSimpleName();

    private IHttpListener mHttpListener;
    private String mUrl;
    private byte[] mBytes;

    @Override
    public void setRequestData(byte[] bytes) {
        mBytes = bytes;
    }

    @Override
    public void setHttpCallBack(IHttpListener httpListener) {
        mHttpListener = httpListener;
    }

    @Override
    public void setURl(String url) {
        mUrl = url;
    }

    @Override
    public void execute() {
        httpConnPost();
    }

    HttpURLConnection urlConn = null;

    private void httpConnPost() {
        URL url = null;
        try {
            url = new URL(mUrl);
            urlConn = (HttpURLConnection) url.openConnection();
            // 设置连接超时时间
            urlConn.setConnectTimeout(5 * 1000);
            //设置从主机读取数据超时
            urlConn.setReadTimeout(5 * 1000);
            // Post请求必须设置允许输出 默认false
            urlConn.setDoOutput(true);
            //设置请求允许输入 默认是true
            urlConn.setDoInput(true);
            // Post请求不能使用缓存
            urlConn.setUseCaches(false);
            // 设置为Post请求
            urlConn.setRequestMethod("POST");
            //设置本次连接是否自动处理重定向
            urlConn.setInstanceFollowRedirects(true);
            // 配置请求Content-Type
            urlConn.setRequestProperty("Content-Type", "application/json");
            // 开始连接
            urlConn.connect();
            // 发送请求参数
            DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
            dos.write(mBytes);
            dos.flush();
            dos.close();
            // 判断请求是否成功
            if (urlConn.getResponseCode() == 200) {
                // 获取返回的数据
                String result = streamToString(urlConn.getInputStream());
                mHttpListener.success(result);
                Log.e(TAG, "Post方式请求成功，result--->" + result);
            } else {
                mHttpListener.failed("Post方式请求失败");
                Log.e(TAG, "Post方式请求失败");
            }
        } catch (Exception e) {
            Log.e(TAG, "httpConnPost: " + e);
            mHttpListener.failed(e.getMessage());
            e.printStackTrace();
        } finally {
            if (urlConn != null) {
                urlConn.disconnect();
            }
        }
    }

    /**
     * 将输入流转换成字符串
     *
     * @param is 从网络获取的输入流
     * @return
     */
    public String streamToString(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            is.close();
            byte[] byteArray = baos.toByteArray();
            return new String(byteArray);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }
}
