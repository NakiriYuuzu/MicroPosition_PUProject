package tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.VolleyError;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.ApiConnect.VolleyApi;

public class BeaconStore {

    private Activity activity;
    private Beacon o1, o2;

    BeaconDefine beaconDefine = new BeaconDefine();
    VolleyApi volleyApi;

    public BeaconStore(Activity activity, Beacon o1) {
        this.activity = activity;
        this.o1 = o1;
    }

    public BeaconStore(Activity activity, Beacon o1, Beacon o2) {
        this.activity = activity;
        this.o1 = o1;
        this.o2 = o2;
    }

    public void beaconData(boolean apiChecked) {
        //0 = major, 1 = minor, 2 = rssi, 3 = distance, 4 = txPower
        ArrayList<String> firstBeacon = new ArrayList<>();
        ArrayList<String> secondBeacon = new ArrayList<>();

        if (!beaconDefine.getLocationMsg(o1.getId2().toString(), o1.getId3().toString()).equals("暂无位置信息")) {
            firstBeacon.add(o1.getId2().toString());
            firstBeacon.add(o1.getId3().toString());
            firstBeacon.add(String.valueOf(o1.getRssi()));
            firstBeacon.add(String.valueOf(o1.getDistance()));
            firstBeacon.add(String.valueOf(o1.getTxPower()));
        }

        if (o2 != null) {
            if (!beaconDefine.getLocationMsg(o2.getId2().toString(), o2.getId3().toString()).equals("暂无位置信息")) {
                secondBeacon.add(o2.getId2().toString());
                secondBeacon.add(o2.getId3().toString());
                secondBeacon.add(String.valueOf(o2.getRssi()));
                secondBeacon.add(String.valueOf(o2.getDistance()));
                secondBeacon.add(String.valueOf(o2.getTxPower()));
            }
        }

        // Sending data
        if (apiChecked) {
            if (firstBeacon.size() > 0) {
                Log.e("Safety", beaconDefine.getLocationMsg(firstBeacon.get(0), firstBeacon.get(1)) + " was sent");
                volleyApi = new VolleyApi(activity, "http://120.110.93.246/CAMEFSC/public/api/scene/1");

                volleyApi.post_API_Safety(new VolleyApi.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                    }

                    @Override
                    public void onFailed(VolleyError error) {

                    }
                });
            }

            if (secondBeacon.size() > 0) {
                Log.e("Safety", beaconDefine.getLocationMsg(secondBeacon.get(0), secondBeacon.get(1)) + " was sent");
                volleyApi = new VolleyApi(activity, "");
            }
        }
    }

    public Beacon getO1() {
        return o1;
    }

    public void setO1(Beacon o1) {
        this.o1 = o1;
    }

    public Beacon getO2() {
        return o2;
    }

    public void setO2(Beacon o2) {
        this.o2 = o2;
    }
}
