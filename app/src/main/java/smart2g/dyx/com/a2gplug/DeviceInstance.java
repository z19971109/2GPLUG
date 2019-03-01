package smart2g.dyx.com.a2gplug;

public class DeviceInstance {

    private int id;

    private String name;

    private String mac;

    private String expireTime;

    private int status;

    private String switchStatus;

    private String timer;

    private String ip;

    private String hardWare ;

    private String softWare;

    private String deviceType;

    public DeviceInstance(int id , String name , String mac , String expireTime , int status , String switchStatus ,
                          String timer , String ip , String hardWare , String softWare , String deviceType){
        this.id = id;
        this.name = name;
        this.mac = mac;
        this.expireTime = expireTime;
        this.status = status;
        this.switchStatus = switchStatus;
        this.timer = timer;
        this.ip = ip;
        this.hardWare = hardWare;
        this.softWare = softWare;
        this.deviceType = deviceType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public int getId() {
        return id;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSwitchStatus() {
        return switchStatus;
    }

    public void setSwitchStatus(String switchStatus) {
        this.switchStatus = switchStatus;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHardWare() {
        return hardWare;
    }

    public void setHardWare(String hardWare) {
        this.hardWare = hardWare;
    }

    public String getSoftWare() {
        return softWare;
    }

    public void setSoftWare(String softWare) {
        this.softWare = softWare;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
