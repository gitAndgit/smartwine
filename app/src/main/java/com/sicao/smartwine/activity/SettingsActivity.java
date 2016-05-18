package com.sicao.smartwine.activity;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sicao.smartwine.BaseActivity;
import com.sicao.smartwine.R;
import com.sicao.smartwine.api.ApiClient;
import com.sicao.smartwine.device.LoginActivity;
import com.sicao.smartwine.user.FeedBackActivity;
import com.sicao.smartwine.util.FileUtils;
import com.sicao.smartwine.util.LToast;
import com.sicao.smartwine.util.LToastUtil;
import com.sicao.smartwine.util.UserInfoUtil;

import java.io.File;
import java.util.List;

/**
 *设置页面
 */
public class SettingsActivity extends BaseActivity {
    TextView tv_login,tv_cache;//登录 /缓存
    RelativeLayout lr_remove_cache,lr_suggest,lr_about_smart;//清除缓存 /给建议 /关于只能酒柜
    @Override
    public String setTitle() {
        return "酒柜设置";
    }

    @Override
    protected int setView() {
        return R.layout.activity_settings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化页面
        initView();
    }
    protected void initView(){
        tv_login=(TextView)findViewById(R.id.tv_login);//登陆与退出登录
        lr_remove_cache=(RelativeLayout)findViewById(R.id.lr_remove_cache);
        lr_suggest=(RelativeLayout)findViewById(R.id.lr_suggest);
        lr_about_smart=(RelativeLayout)findViewById(R.id.lr_about_smart);
        tv_cache=(TextView) findViewById(R.id.tv_cache);
        tv_cache.setText(calCache());
        lr_remove_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv_cache.getText().toString().trim().equals("0KB")){
                    LToastUtil.show(SettingsActivity.this,"当前缓存为空");
                }else{
                    cleanCache();
                }
            }
        });
        lr_suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,FeedBackActivity.class));
            }
        });
        lr_about_smart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LToastUtil.show(SettingsActivity.this,"正在开发，请期待");
            }
        });
        isLogin(tv_login);
    }
    /**
     * 计算缓存大小
     *
     * @return
     */
    private String calCache() {
        long fileSize = 0;
        String cacheSize = "0KB";
        File filesDir = getFilesDir();
        fileSize += FileUtils.getDirSize(filesDir);
        if (fileSize > 0)
            cacheSize = FileUtils.formatFileSize(fileSize);
        return cacheSize;
    }
    /**
     * 清除缓存
     */
    public void cleanCache() {
        appContext.cleanApp();
        deleteDatabase("webview.db");
        deleteDatabase("webview.db-shm");
        deleteDatabase("webview.db-wal");
        deleteDatabase("webviewCache.db");
        deleteDatabase("webviewCache.db-shm");
        deleteDatabase("webviewCache.db-wal");
        appContext.clearCacheFolder(getFilesDir(), System.currentTimeMillis());
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            appContext.clearCacheFolder(this.getExternalCacheDir(),
                    System.currentTimeMillis());
        }
        tv_cache.setText("0KB");
    }
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }
    //判断是否登陆
    public void isLogin(TextView view){
        if(UserInfoUtil.getLogin(this)){
            view.setText("退出");
            view.setTextColor(getResources().getColor(R.color.d3d3d3d));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApiClient.logout(SettingsActivity.this);
                    tv_login.setVisibility(View.GONE);
                    finish();
                }
            });
        }else{
            tv_login.setVisibility(View.GONE);
//            view.setText("登陆");
//            view.setTextColor(getResources().getColor(R.color.baseColor));
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
//                }
//            });
        }
    }
}
