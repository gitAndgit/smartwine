package com.sicao.smartwine;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sicao.smartwine.libs.DeviceDiscoveryManager;
import com.sicao.smartwine.libs.DeviceUtil;
import com.smartline.life.core.LifeApplication;

/**
 * Created by techssd on 2015/12/25.
 */
public class AppContext extends LifeApplication {

    // 酒柜部分
    private DeviceDiscoveryManager manager;
    public static ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
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
}
