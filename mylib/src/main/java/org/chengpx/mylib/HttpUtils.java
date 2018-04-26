package org.chengpx.mylib;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * create at 2018/4/23 14:50 by chengpx
 */
public class HttpUtils {

    private static HttpUtils sHttpUtils = new HttpUtils();

    private final OkHttpClient mOkHttpClient;
    private String tag = "org.chengpx.a2trafficlight.util.HttpUtils";

    private HttpUtils() {
        mOkHttpClient = new OkHttpClient();
    }

    /**
     * 发送一个 post 请求
     *
     * @param url      协议接口
     * @param req      请求参数
     * @param callBack 回调接口
     * @param <Req>    请求参数封装体类型
     */
    public <Req> void sendPost(String url, Req req, final Callback callBack) {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), SignalInstancePool.newGson().toJson(req)))
                .build();
        mOkHttpClient.newCall(request).enqueue(new okhttp3.Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(tag, Thread.currentThread().getName());
                Message message = Message.obtain();
                message.obj = response.body().string();
                if (callBack != null) {
                    callBack.sendMessage(message);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

        });

    }

    /**
     * 返回实例对象
     *
     * @return 单例对象
     */
    public static HttpUtils getInstance() {
        return sHttpUtils;
    }

    /**
     * HttpUtils 成功回调接口
     *
     * @param <Result> 结果数据封装体类型
     */
    public static abstract class Callback<Result> extends Handler {

        private final Class<Result> mResultClass;

        /**
         * @param resultClass 结果数据封装体类型字节码
         */
        public Callback(Class<Result> resultClass) {
            mResultClass = resultClass;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Object obj = msg.obj;
            if (!(obj instanceof String)) {
                return;
            }
            String json = (String) obj;
            try {
                JSONObject jsonObject = new JSONObject(json);
                String serverInfo = jsonObject.getString("serverInfo");
                if (serverInfo != null) {
                    onSuccess(SignalInstancePool.newGson().fromJson(serverInfo, mResultClass));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 请求成功后调用
         *
         * @param result 结果数据封装体
         */
        protected abstract void onSuccess(Result result);

    }

}
