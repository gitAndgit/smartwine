package com.sicao.smartwine;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.sicao.smartwine.libs.DeviceDiscoveryManager;
import com.sicao.smartwine.libs.DeviceUtil;
import com.sicao.smartwine.shop.entity.ShareEntity;
import com.sicao.smartwine.util.StringUtil;
import com.smartline.life.core.LifeApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.theme.OnekeyShare;
import cn.sharesdk.onekeyshare.theme.OnekeyShareTheme;

/**
 * Created by techssd on 2015/12/25.
 */
public class AppContext extends LifeApplication {
    private static AppContext  instance;
    // 酒柜部分
    private DeviceDiscoveryManager manager;
    //手机分辨率参数
    /** 窗口管理 **/
    private WindowManager mManager = null;
    /** 屏幕数据 **/
    public static DisplayMetrics metrics = null;
    // 下订单支付流程页面记录、
    public static ArrayList<Activity> pay = new ArrayList<Activity>();
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        // 初始化屏幕参数
        mManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        metrics = new DisplayMetrics();
        mManager.getDefaultDisplay().getMetrics(metrics);
        // 酒柜部分
        DeviceUtil.loadConfig(getApplicationContext(), R.xml.devices);
        DeviceUtil.loadConfigType(getApplicationContext(), R.xml.types);
        manager = new DeviceDiscoveryManager(getApplicationContext());
        manager.startDiscovery();
        //faceBook图片加载库
        Fresco.initialize(this);
    }
    /**
     * 判断网络是否可用
     *
     * @return
     */
    public static boolean isNetworkAvailable() {
        NetworkInfo info = getNetworkInfo();
        if (info != null) {
            return info.isAvailable();
        }
        return false;
    }

    private static NetworkInfo getNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager)instance.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    // 生成图像名字
    public static String getRandomString(int length) { // length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789"; // 生成字符串从此序列中取
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    /***
     * 分享
     *
     * @param context
     * @param share
     */
    public static void share(final Context context, final ShareEntity share,
                             PlatformActionListener listener) {
        String title = share.getTitle();
        ShareSDK.initSDK(context);
        OnekeyShare oks = new OnekeyShare();
        oks.setTheme(OnekeyShareTheme.CLASSIC);
        oks.setDialogMode();
        // 在自动授权时可以禁用SSO方式
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(share.getUrl());
        oks.setImageUrl(share.getCover());
        // text是分享文本，所有平台都需要这个字段
        oks.setText(share.getContent() + share.getUrl());
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(share.getUrl());
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(share.getUrl());
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(share.getUrl());
        // 启动分享GUI
        oks.show(context);
        oks.setCustomerLogo(BitmapFactory.decodeResource(
                        context.getResources(), R.drawable.oks_copy_http),
                BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.oks_copy_http), "复制链接",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringUtil.copyToClipBoard(context, share.getUrl());
                        Toast.makeText(instance, "已将内容复制到粘贴板", Toast.LENGTH_SHORT).show();
                    }
                });
        if (null != listener) {
            oks.setCallback(listener);
        }
    }
    public void cleanApp() {
        // 清除数据缓存
        clearCacheFolder(getFilesDir(), System.currentTimeMillis());
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infoList = am.getRunningAppProcesses();
        long beforeMem = getAvailMemory(this);
        Log.i("yaya", "清理前内存---->>>" + beforeMem);
        if (infoList != null) {
            for (int i = 0; i < infoList.size(); ++i) {
                ActivityManager.RunningAppProcessInfo appProcessInfo = infoList.get(i);
                // importance 该进程的重要程度 分为几个级别，数值越低就越重要。
                // 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
                // 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
                if (appProcessInfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    String[] pkgList = appProcessInfo.pkgList;
                    for (int j = 0; j < pkgList.length; ++j) {// pkgList
                        // 得到该进程下运行的包名
                        am.killBackgroundProcesses(pkgList[j]);
                    }
                }

            }
        }

        long afterMem = getAvailMemory(this);
        Log.i("yaya", "清理后内存---->>>" + afterMem);
    }
    /**
     * 清除缓存目录
     *
     * @param dir
     *            目录
     *            当前系统时间
     * @return
     */
    public int clearCacheFolder(File dir, long curTime) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, curTime);
                    }
                    if (child.lastModified() < curTime) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }
    // 获取可用内存大小
    private long getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        // mi.availMem; 当前系统的可用内存
        // return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
        Log.i("yaya", "可用内存---->>>" + mi.availMem / (1024 * 1024));
        return mi.availMem / (1024 * 1024);
    }
}
