package com.iflytek.test1;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.webview);
        init();
        webView.loadUrl("http://wj.ahga.gov.cn/bingo-jmt-business/modules/yjnc/index.html");

    }

    private void init() {
        webView = (WebView) findViewById(R.id.webview);
        webView.setInitialScale(0);
        webView.setVerticalScrollBarEnabled(false);
        webView.getSettings().setJavaScriptEnabled(true);
        //启用数据库
        WebSettings settings = webView.getSettings();
        settings.setDatabaseEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);


        //设置定位的数据库路径
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setGeolocationDatabasePath(dir);
        settings.setSaveFormData(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        //最重要的方法，一定要设置，这就是出不来的主要原因
        settings.setDomStorageEnabled(true);

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSaveFormData(false);
        settings.setSavePassword(false);
        settings.setDomStorageEnabled(true);

        // Enable built-in geolocation
        settings.setGeolocationEnabled(true);

        settings.setAppCacheMaxSize(5 * 1048576);
        settings.setAppCachePath(dir);
        settings.setAppCacheEnabled(true);

        webView.setWebViewClient(new WebViewClient(){});
        webView.setWebChromeClient(new WebChromeClient() {
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
        });


        loadUrlwithPermissions(webView);

    }

    //需要获取的权限
    String[] callPermissions = new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int MY_PERMISSIONS_REQUEST = 110;
    private void loadUrlwithPermissions(WebView webView) {
        // andriod 6.0权限动态添加 判断是否大于6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> needPermissionList = new ArrayList<>();
            for (int i = 0; i < callPermissions.length; i++) {
                if (checkSelfPermission(callPermissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    needPermissionList.add(callPermissions[i]);
                }
            }
            String[] needPremission = getNeedPermission(needPermissionList);
            if (needPremission.length > 0) {
                requestPermissions(needPremission, MY_PERMISSIONS_REQUEST);
            } else {
                initWelcome();
            }
        } else {
            initWelcome();
        }
    }

    private String[] getNeedPermission(List<String> needPermissionList) {
        String[] needRequestPermission = new String[needPermissionList.size()];
        for (int i = 0; i < needPermissionList.size(); i++) {
            needRequestPermission[i] = needPermissionList.get(i);
        }
        return needRequestPermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            List<String> unGetPermissionList = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    unGetPermissionList.add(permissions[i]);
                }
            }
        }
    }

    private void initWelcome() {

    }
}
