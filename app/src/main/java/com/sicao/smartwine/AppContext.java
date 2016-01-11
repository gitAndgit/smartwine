package com.sicao.smartwine;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sicao.smartwine.libs.DeviceDiscoveryManager;
import com.sicao.smartwine.libs.DeviceUtil;
import com.smartline.life.core.LifeApplication;

import java.util.Random;

/**
 * Created by techssd on 2015/12/25.
 */
public class AppContext extends LifeApplication {
    private static AppContext  instance;
    // 酒柜部分
    private DeviceDiscoveryManager manager;
    //图片加载库
    public static ImageLoader imageLoader = ImageLoader.getInstance();
    //手机分辨率参数
    /** 窗口管理 **/
    private WindowManager mManager = null;
    /** 屏幕数据 **/
    public static DisplayMetrics metrics = null;

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
        initImageLoader(this);
    }

    /**
     * 初始化ImageLoader
     *
     * @param context
     */
    private static void initImageLoader(Context context) {

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
                context);
        config.memoryCacheSize(25 * 1024 * 1024);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        // 5个线程下载
        config.threadPoolSize(5);
        config.tasksProcessingOrder(QueueProcessingType.FIFO);
        imageLoader.init(config.build());
    }

    /**
     * 轮播图页
     */
    public static DisplayImageOptions gallery = new DisplayImageOptions.Builder()
            .cacheInMemory(true).cacheOnDisk(true)
            .showImageOnLoading(R.drawable.ic_launcher)
            .showImageForEmptyUri(R.drawable.ic_launcher)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .showImageOnFail(R.drawable.ic_launcher)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

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
}
