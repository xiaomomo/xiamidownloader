package com.youyu.xiamidownloader.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import com.youyu.xiamidownloader.R;
import com.youyu.xiamidownloader.domain.UserLoginInfo;
import org.apache.commons.lang.StringUtils;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        UserLoginInfo userLoginInfo = getUserLoginInfo();
        boolean loginEd = !StringUtils.isBlank(userLoginInfo.getToken());
        if (!loginEd) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        WebView webview = (WebView) findViewById(R.id.mainWebView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("file:///android_asset/h5/html/main.html");
        webview.addJavascriptInterface(new Object() {

            @JavascriptInterface
            public void changeAccount() {
            }

            @JavascriptInterface
            public void newDownload() {
                startActivity(new Intent(MainActivity.this, NewDownloadActivity.class));
            }

            @JavascriptInterface
            public void downloadQueue() {
            }

            @JavascriptInterface
            public void downloadList() {
            }
        }, "navJs");


    }

    private UserLoginInfo getUserLoginInfo() {
        SharedPreferences userLoginInfoPre = getSharedPreferences("userLoginInfo", 0);
        UserLoginInfo userLoginInfo = new UserLoginInfo();
        userLoginInfo.setEmail(userLoginInfoPre.getString("email", ""));
        userLoginInfo.setPassword(userLoginInfoPre.getString("password", ""));
        userLoginInfo.setUserId(userLoginInfoPre.getString("userId", ""));
        userLoginInfo.setToken(userLoginInfoPre.getString("token", ""));
        userLoginInfo.setExpire(userLoginInfoPre.getString("expire", ""));
        return userLoginInfo;
    }
}
