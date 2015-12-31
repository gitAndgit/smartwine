package com.sicao.smartwine;

import com.sicao.smartwine.libs.DeviceDiscoveryManager;
import com.sicao.smartwine.libs.DeviceUtil;
import com.smartline.life.core.LifeApplication;

/**
 * Created by techssd on 2015/12/25.
 */
public class AppContext extends LifeApplication {

    // 酒柜部分
    private DeviceDiscoveryManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        // 酒柜部分
        DeviceUtil.loadConfig(getApplicationContext(), R.xml.devices);
        DeviceUtil.loadConfigType(getApplicationContext(), R.xml.types);
        manager = new DeviceDiscoveryManager(getApplicationContext());
        manager.startDiscovery();
    }
}
