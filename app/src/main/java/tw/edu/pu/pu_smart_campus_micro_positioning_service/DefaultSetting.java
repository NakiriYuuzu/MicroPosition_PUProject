package tw.edu.pu.pu_smart_campus_micro_positioning_service;

import org.altbeacon.beacon.Region;

public class DefaultSetting {
    /**
     * Api Url Setting
     */

    //LoginActivity
    public static String API_LOGIN = "http://140.128.10.144:54080/public/api/login/user";
    public static String API_GUESTLOGIN = "http://140.128.10.144:54080/public/api/login/tourist";

    //GuideActivity
    public static String API_GUIDE = "http://140.128.10.144:54080/public/api/scene/";

    //CheckActivity
    public static String API_CHECK = "http://140.128.10.144:54080/public/api/people";

    //SafetyActivity
    public static String API_SAFETY_MONITOR_START = "http://140.128.10.144:54080/public/api/monitor/start";
    public static String API_SAFETY_MONITOR_END = "http://140.128.10.144:54080/public/api/monitor/end";
    public static String API_SAFETY_SOS_START = "http://140.128.10.144:54080/public/api/monitor/sos";
    public static String API_SAFETY_SOS_END = "http://140.128.10.144:54080/public/api/monitor/sosend";


    /**
     * Beacon Settings
     */
    public static final Region REGION = new Region("REGION_BEACON_01", null, null, null);
}
