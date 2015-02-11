package com.kunboy.weiboclient.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kunboy.weiboclient.R;
import com.kunboy.weiboclient.account.AccessTokenKeeper;
import com.kunboy.weiboclient.constants.WeiboConstants;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.LogUtil;
import com.squareup.picasso.Picasso;

/**
 * Created by sunhongkun on 2015/2/11.
 */
public class WelcomeActivity extends Activity {


    private final String TAG = "WelcomeActivity";

    private boolean hasCallMainActivity = false;
    private Oauth2AccessToken mAccessToken;
    private AuthInfo mAuthInfo;
    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private SsoHandler mSsoHandler;

    private ImageView mUserIcon;
    private TextView mNickName;
    private RelativeLayout userLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        // 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
        mAuthInfo = new AuthInfo(this, WeiboConstants.APP_KEY, WeiboConstants.REDIRECT_URL, WeiboConstants.SCOPE);
        mSsoHandler = new SsoHandler(WelcomeActivity.this, mAuthInfo);
        findViews();
    }

    private void findViews() {
        mUserIcon = (ImageView) findViewById(R.id.profile_image);
        mNickName = (TextView) findViewById(R.id.nickName);
        userLayout = (RelativeLayout) findViewById(R.id.welcome_user_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAccessToken = AccessTokenKeeper.readAccessToken(WelcomeActivity.this);
        if (TextUtils.isEmpty(mAccessToken.getUid()) || TextUtils.isEmpty(mAccessToken.getToken())) {
            mSsoHandler.authorize(new AuthListener());
        } else {
            if (mAccessToken.getExpiresTime() <= System.currentTimeMillis()) {
                mSsoHandler.authorize(new AuthListener());
            } else {
                getUserInfo();
            }

        }
    }

    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link com.sina.weibo.sdk.auth.sso.SsoHandler#authorizeCallBack} 后，
     * 该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // 显示 Token
//                updateTokenView(false);

                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(WelcomeActivity.this, mAccessToken);
                Toast.makeText(WelcomeActivity.this,
                        "授权成功", Toast.LENGTH_SHORT).show();
                getUserInfo();
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = "授权失败";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(WelcomeActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }


        @Override
        public void onCancel() {
            Toast.makeText(WelcomeActivity.this,
                    "取消授权", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(WelcomeActivity.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     *
     * @see {@link Activity#onActivityResult}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 获取用户信息，主要包括头像，昵称
     */
    private void getUserInfo() {
        UsersAPI usersAPI = new UsersAPI(WelcomeActivity.this, WeiboConstants.APP_KEY, mAccessToken);
        long uid = Long.parseLong(mAccessToken.getUid());
        usersAPI.show(uid, mListener);
    }

    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                // 调用 User#parse 将JSON串解析成User对象
                User user = User.parse(response);
                if (user != null) {

                    if (!TextUtils.isEmpty(user.profile_image_url)) {
                        Picasso.with(WelcomeActivity.this).load(user.profile_image_url).into(mUserIcon);
                    }
                    mNickName.setText(user.name);
                    userLayout.setVisibility(View.VISIBLE);
                    ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(userLayout, "translationY", 0f, -300);
                    mAnimatorTranslateY.setDuration(1000).addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if(!hasCallMainActivity){
                                Log.d("kunboy","动画结束!");
                                Toast.makeText(WelcomeActivity.this, "欢迎回来!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.setClass(WelcomeActivity.this, MainActivity.class);
                                WelcomeActivity.this.startActivity(intent);
                                hasCallMainActivity = true;
                                WelcomeActivity.this.finish();
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    mAnimatorTranslateY.start();

//                    Toast.makeText(WelcomeActivity.this, "欢迎回来!", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent();
//                    intent.setClass(WelcomeActivity.this, MainActivity.class);
//                    WelcomeActivity.this.startActivity(intent);
//                    WelcomeActivity.this.finish();
                } else {
                    Toast.makeText(WelcomeActivity.this, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(WelcomeActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };
}
