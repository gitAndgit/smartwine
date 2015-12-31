package com.sicao.smartwine.libs;

import com.smartline.life.device.Device;

/**
 * Created by putao on 2015/8/27.
 */
public class WineCabineEntity extends Device {
    //设置温度
    private String setTemp;
    //实际温度
    private String realTemp;
    //大灯
    private boolean openLight;
    //设备开关
    private boolean open;

    public void setRealTemp(String realTemp) {
        this.realTemp = realTemp;
    }

    public String getRealTemp() {
        return realTemp;
    }

    public void setSetTemp(String setTemp) {
        this.setTemp = setTemp;
    }

    public String getSetTemp() {
        return setTemp;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpenLight(boolean openLight) {
        this.openLight = openLight;
    }

    public boolean isOpenLight() {
        return openLight;
    }
}
