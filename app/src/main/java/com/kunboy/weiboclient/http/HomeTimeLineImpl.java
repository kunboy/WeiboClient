package com.kunboy.weiboclient.http;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by sunhongkun on 2015/2/11.
 */
public class HomeTimeLineImpl extends AbstrackOpenAPIRequest {

    private HashMap<String,String> params = new LinkedHashMap<String,String>();

    private String sinceID = "0";
    private String maxID = "0";
    private int count = 20;
    private int page = 1;
    private int baseAPP;
    private int feature;
    private int trimUser;


    /**
     *
     * @param accessToken
     * @param sinceId 若指定此参数，则返回ID比since_id大的微博（即比since_id时间晚的微博），默认为0
     * @param maxID 若指定此参数，则返回ID小于或等于max_id的微博，默认为0。
     * @param count 单页返回的记录条数，最大不超过100，默认为20。
     * @param page 返回结果的页码，默认为1。
     * @param baseAPP 是否只获取当前应用的数据。0为否（所有数据），1为是（仅当前应用），默认为0。
     * @param feature 过滤类型ID，0：全部、1：原创、2：图片、3：视频、4：音乐，默认为0。
     * @param trimUser 返回值中user字段开关，0：返回完整user字段、1：user字段仅返回user_id，默认为0。
     */
    public HomeTimeLineImpl(Oauth2AccessToken accessToken,String sinceId,String maxID,int count,int page,int baseAPP,int feature,int trimUser) {
        super(accessToken);
        this.sinceID = sinceId;
        this.maxID = maxID;
        this.count = count;
        this.page = page;
        this.baseAPP = baseAPP;
        this.feature = feature;
        this.trimUser = trimUser;
    }

    @Override
    String getScriptName() {
        return "/statuses/friends_timeline.json";
    }

    @Override
    String getMethod() {
        return AbstrackOpenAPIRequest.HTTPMETHOD_GET;
    }

    @Override
    HashMap<String, String> getSpecialParams() {
        params.put(KEY_ACCESS_TOKEN,mAccessToken.getToken());
        params.put("since_id",sinceID);
        params.put("max_id",maxID);
        params.put("count",""+count);
        params.put("page",""+page);
        params.put("base_app",""+baseAPP);
        params.put("feature",""+feature);
        params.put("trim_user",""+trimUser);
        return params;
    }
}
