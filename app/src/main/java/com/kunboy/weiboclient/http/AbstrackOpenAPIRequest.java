package com.kunboy.weiboclient.http;

import android.content.Context;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by sunhongkun on 2015/1/29.
 */
public abstract class AbstrackOpenAPIRequest {


    private Request mRequest;

    /**
     * 访问微博服务接口的地址
     */
    protected static final String API_SERVER = "https://api.weibo.com/2";
    /**
     * POST 请求方式
     */
    protected static final String HTTPMETHOD_POST = "POST";
    /**
     * GET 请求方式
     */
    protected static final String HTTPMETHOD_GET = "GET";
    /**
     * HTTP 参数
     */
    protected static final String KEY_ACCESS_TOKEN = "access_token";

    /**
     * 当前的 Token
     */
    protected Oauth2AccessToken mAccessToken;


    public AbstrackOpenAPIRequest(Oauth2AccessToken accessToken) {
        this.mAccessToken = accessToken;
    }


    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    /**
     * 返回OpenApi Cgi名字
     * 例子：/statuses/home_timeline
     *
     * @return 指定OpenApi Cgi名字
     */
    abstract String getScriptName();

    /**
     * 请求方法，"get" 或者 "post"
     *
     * @return
     */
    abstract String getMethod();

    private String getUrlString() {
        // 指定OpenApi Cgi名字
        String scriptName = getScriptName();
        StringBuilder sb = new StringBuilder(API_SERVER);
        sb.append(scriptName);
        String url = sb.toString();
        return url;
    }


    abstract HashMap<String, String> getSpecialParams();

    /**
     * 这里使用了HttpClinet的API。只是为了方便
     *
     * @param params
     * @return
     */
    private String formatParams(List<BasicNameValuePair> params) {
        String s = URLEncodedUtils.format(params, "UTF-8");
        return s;
    }

    /**
     * 为HttpGet 的 url 方便的添加多个name value 参数。
     *
     * @param url
     * @param params
     * @return
     */
    private String attachHttpGetParams(String url, List<BasicNameValuePair> params) {
        return url + "?" + formatParams(params);
    }

    /**
     * 为HttpGet 的 url 方便的添加1个name value 参数。
     *
     * @param url
     * @param name
     * @param value
     * @return
     */
    private String attachHttpGetParam(String url, String name, String value) {
        return url + "?" + name + "=" + value;
    }


    /**
     * 生成步骤：
     * 2.获取不同OpenApi特殊参数
     * 3.通过所有参数获取签名秘钥并加入到参数列表中
     * 4.将参数添加到post或者get请求参数中
     *
     * @return Request
     */
    public Request buildRequest() {
        HashMap<String, String> params = getSpecialParams();

        Request.Builder requestBuilder = null;
        if (getMethod().equals(HTTPMETHOD_GET)) {
            List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
            String url = getUrlString();
            for (Iterator it = params.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, String> e = (Map.Entry<String, String>) it.next();
//                url = url+e.getKey()+"="+e.getValue()+"&";
                list.add(new BasicNameValuePair(e.getKey(),e.getValue()));
            }
            url = attachHttpGetParams(url,list);
            requestBuilder = new Request.Builder().url(url).tag(getScriptName());
            mRequest = requestBuilder.build();
        } else {
            requestBuilder = new Request.Builder().url(getUrlString()).tag(getScriptName());
            FormEncodingBuilder builder = new FormEncodingBuilder();
            for (Iterator it = params.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry e = (Map.Entry) it.next();
                builder.add((String) e.getKey(), (String) e.getValue());
            }
            RequestBody body = builder.build();
            try {
                params.remove(KEY_ACCESS_TOKEN);
                //这里设置了Tag（getScriptName），便于后面的call操作的key
                mRequest = requestBuilder.header(KEY_ACCESS_TOKEN, mAccessToken.getToken()).url(getUrlString()).post(body).build();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return mRequest;
    }

    /**
     * 把数据源HashMap转换成json
     *
     * @param map
     */
    private static String hashMapToJsonString(HashMap map) {
        StringBuffer sb = new StringBuffer("{");
        for (Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry e = (Map.Entry) it.next();
            sb.append("'").append(e.getKey()).append("':").append("'").append(e.getValue()).append("',");
        }
        String jsonString = sb.substring(0, sb.lastIndexOf(","));
        return jsonString + "}";
    }

}
