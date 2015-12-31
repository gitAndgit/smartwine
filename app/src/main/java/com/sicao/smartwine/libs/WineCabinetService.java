package com.sicao.smartwine.libs;

import com.smartline.life.iot.IoTService;

import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by tom on 15/7/17.
 */
public class WineCabinetService extends IoTService {

    public static final String SERVICE_NAEME = "WineCabinet";

    private boolean on;

    private boolean light;

    private boolean compressor;

    private int temp;

    private int realTemp;

    public WineCabinetService(){
        super(SERVICE_NAEME);
    }

    public WineCabinetService(IoTService service){
        super(SERVICE_NAEME);
        this.on = service.getBoolean("on");
        this.light = service.getBoolean("light");
        this.compressor = service.getBoolean("compressor");
        this.temp = service.getInt("temp");
        this.realTemp = service.getInt("realTemp");
    }

    public WineCabinetService(String jid,XMPPConnection connection){
        super(SERVICE_NAEME,jid,connection);
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
        putBoolean("on",on);
    }

    public boolean isLight() {
        return light;
    }

    public void setLight(boolean light) {
        this.light = light;
        putBoolean("light",light);
    }

    public int getRealTemp() {
        return realTemp;
    }

    public void setRealTemp(int realTemp) {
        this.realTemp = realTemp;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
        putInt("temp",temp);
    }

    public boolean isCompressorWorking() {
        return compressor;
    }

    public void setCompressorWorking(boolean compressor) {
        this.compressor = compressor;
    }
}
