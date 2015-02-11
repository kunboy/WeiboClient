package com.kunboy.weiboclient.http;

import android.os.Handler;
import android.os.Looper;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * 该类对OkHttp中的Callback进行了封装
 * 主要逻辑是用Handler将doFailure以及doResponse方法在主线程中执行
 * pares方法是抽象的，用于不同的解析返回不同的解析结果
 * Created by sunhongkun on 2015/1/30.
 */
public abstract class AbstractOkHttpCallBack implements Callback {
    private Handler mHandler = new Handler(Looper.getMainLooper());
    @Override
    public void onFailure(final Request request, final IOException e) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                //出错情况，会将request以及IOException抛出
                doFailure(request,e);
            }
        });
    }

    /**
     * 在非主线程中处理，首先判定是否响应成功，再次调用parse解析数据
     * 然后通过handler在主线程中调用doResponse方法
     * @param response
     * @throws java.io.IOException
     */
    @Override
    public void onResponse(Response response) throws IOException {

        //没有成功返回，直接抛出IOException
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        //解析数据，非主线程
        final Object object = parse(response);
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                doResponse(object);
            }
        });
    }

    /**
     * 通过Response解析出想要的数据
     * @param response
     * @return
     */
    public abstract Object parse(Response response);

    /**
     * 在主线程中处理
     * @param request
     * @param ioexception
     */
    public abstract void doFailure(Request request, IOException ioexception);

    /**
     * 在主线程中处理
     * @param object parse解析出来的数据
     */
    public abstract void doResponse(Object object);
}
