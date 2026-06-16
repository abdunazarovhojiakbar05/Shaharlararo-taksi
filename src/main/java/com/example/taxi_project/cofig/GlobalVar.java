package com.example.taxi_project.cofig;

public class GlobalVar {

    private static final ThreadLocal<String> deviceId = ThreadLocal.withInitial(String::new);
    private static final  ThreadLocal<String> deviceName = ThreadLocal.withInitial(String::new);
    private static final  ThreadLocal<String> platform = ThreadLocal.withInitial(String::new);
    private  static final  ThreadLocal<String> appVersion = ThreadLocal.withInitial(String::new);

    public static String getDeviceId() {return deviceId.get();}
    public static String getDeviceName() {return deviceName.get();}
    public static String getPlatform() {return platform.get();}
    public static String getAppVersion() {return appVersion.get();}

    public static void setDeviceId(String deviceId) {GlobalVar.deviceId.set(deviceId);}
    public static void setDeviceName(String deviceName) {GlobalVar.deviceName.set(deviceName);}
    public static void setPlatform(String platform) {GlobalVar.platform.set(platform);}
    public static void setAppVersion(String appVersion) {GlobalVar.appVersion.set(appVersion);}


    public static void removeAll() {
        deviceId.remove();
        deviceName.remove();
        platform.remove();
        appVersion.remove();
    }
}
