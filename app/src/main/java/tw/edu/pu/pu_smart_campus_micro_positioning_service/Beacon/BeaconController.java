package tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon;

import static tw.edu.pu.pu_smart_campus_micro_positioning_service.DefaultSetting.REGION;

import android.app.Activity;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.Collections;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.RequestHelper;

public class BeaconController {

    private static final String TAG = "BeaconController";

    private static final long DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD = 1000L;
    private static final long DEFAULT_FOREGROUND_SCAN_PERIOD = 1000L;

    //Settings
    private final Activity activity;
    private Beacon beacon;
    private BeaconManager beaconManager;
    private BeaconTransmitter beaconTransmitter;

    //Broadcast_Beacon
    private final BeaconParser beaconParser = new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25")
            .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24");

    // TODO: Under is method

    public BeaconController(Activity activity) {
        this.activity = activity;
    }

    public void initBeacon() {
        beaconManager = BeaconManager.getInstanceForApplication(activity);

        //beacon AddStone m:0-3=4c000215 or alt beacon = m:2-3=0215
        beaconManager.getBeaconParsers().add(beaconParser);

        beaconManager.setForegroundBetweenScanPeriod(DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD);
        beaconManager.setForegroundScanPeriod(DEFAULT_FOREGROUND_SCAN_PERIOD);


    }

    public void startScanning(BeaconModify beaconModify) {
        beaconManager.addRangeNotifier(beaconModify::modifyBeacon);
        beaconManager.startRangingBeacons(REGION);
    }

    public void stopScanning() {
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.stopRangingBeacons(REGION);
        beaconManager.removeAllRangeNotifiers();
    }

    public void init_BroadcastBeacon() {
        beacon = new Beacon.Builder()
                .setId1("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6")
                .setId2("1")
                .setId3("2")
                .setManufacturer(0x0118)
                .setTxPower(-79)
                .setDataFields(Collections.singletonList(0L))
                .build();

        beaconTransmitter = new BeaconTransmitter(activity, beaconParser);
    }

    public void start_BroadcastBeacon() {
        beaconTransmitter.startAdvertising(beacon);
    }

    public void stop_BroadcastBeacon() {
        beaconTransmitter.stopAdvertising();
    }

    public interface BeaconModify {
        void modifyBeacon(Collection<Beacon> beacons, Region region);
    }
}

