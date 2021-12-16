package tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.permissionx.guolindev.PermissionX;

import org.altbeacon.bluetooth.BluetoothMedic;


public class RequestHelper {

    private final Activity activity;

    public RequestHelper(Activity activity) {
        this.activity = activity;
    }

    public void requestBluetooth() {
        BluetoothAdapter mBluetoothAdapter;

        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(activity, "您的手機無法使用該應用...", Toast.LENGTH_SHORT).show();

        }

        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(activity, "您的手機無法使用該應用...", Toast.LENGTH_SHORT).show();

        }

        final BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivity(enableBluetooth);
        }
    }

    @SuppressLint("HardwareIds")
    public String requestIMEI() {
        return Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public void flushBluetooth() {
        BluetoothMedic medic = BluetoothMedic.getInstance();
        medic.enablePowerCycleOnFailures(activity);
        medic.enablePeriodicTests(activity, BluetoothMedic.SCAN_TEST |
                BluetoothMedic.TRANSMIT_TEST);
    }

    public void requestPermission() {
        if (Build.VERSION.SDK_INT > 23) {
            PermissionX.init((FragmentActivity) activity)
                    .permissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

                    .onExplainRequestReason((scope, deniedList) -> scope.showRequestReasonDialog(
                            deniedList, "Grant Permission!", "Sure", "Cancel"))

                    .request((allGranted, grantedList, deniedList) -> {
                        if (!allGranted) {
                            Toast.makeText(activity, "Grant Permission failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(activity, "您的手機無法使用該應用...", Toast.LENGTH_SHORT).show();
            activity.finish();
        }
    }
}
