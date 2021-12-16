package tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon;

import android.app.Activity;
import android.util.Log;

import com.android.volley.VolleyError;

import org.altbeacon.beacon.Beacon;

import java.util.HashMap;
import java.util.Map;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.ApiConnect.VolleyApi;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.ShareData;

public class BeaconStore {
    private final String TAG = "beaconStore: ";

    private final Activity activity;
    private final ShareData shareData;

    VolleyApi volleyApi;

    public BeaconStore(Activity activity) {
        this.activity = activity;
        shareData = new ShareData(activity);
    }

    public void beaconData(Boolean apiChecked, Beacon o1, String apiURL) {
        Log.e(TAG, o1.toString());
        if (apiChecked) {
            // Sending data

            Log.e(TAG, "data was sent.");
            volleyApi = new VolleyApi(activity, apiURL);

            volleyApi.post_API_Safety_Start(new VolleyApi.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.e(TAG, result);
                }

                @Override
                public void onFailed(VolleyError error) {
                    Log.e(TAG, error.toString());
                }

            }, () -> {
                Map<String, String> params = new HashMap<>();
                params.put("uid", shareData.getUID());
                params.put("major", o1.getId2().toString());
                params.put("minor", o1.getId3().toString());
                params.put("txpower", String.valueOf(o1.getTxPower()));
                params.put("rssi", String.valueOf(o1.getRssi()));
                params.put("distance", String.valueOf(o1.getDistance()));
                return params;
            });
        }
    }
}
