package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.Button;

import com.airbnb.lottie.L;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon.BeaconDefine;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.Database.DBHelper;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;

public class GuideActivity extends AppCompatActivity implements BeaconConsumer, OnMapReadyCallback {

    private final String TAG = "SafetyActivity: ";
    private final String IPHONE_ID = "594650a2-8621-401f-b5de-6eb3ee398170";
    private final String IBEACON_UUID = "699ebc80-e1f3-11e3-9a0f-0cf3ee3bc012";

    private static final long DEFAULT_FOREGROUND_SCAN_PERIOD = 500L; // half sec
    private static final long DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD = 500L; // half sec

    private final float DISTANCE_THRESHOLD = 3f;
    private final float DISTANCE_THRESHOLD_DATA = 1.5f;

    private boolean beaconIsRunning = false;

    private boolean spot01Enter = false;
    private boolean spot02Enter = false;
    private boolean spot03Enter = false;

    private GoogleMap gMap;

    private BeaconManager beaconManager;
    private BeaconDefine beaconDefine;

    private MaterialButton btnStart, btnStop;
    private Button btn_spot01, btn_spot02, btn_spot03;
    private MaterialTextView tvShowDisplay;

    private ArrayList<HashMap<String, String>> beaconMap = new ArrayList<>();
    private HashMap<String, Integer> regionMap = new HashMap<>();
    private ArrayList<Beacon> beaconList = new ArrayList<>();

    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {

        }
    };

    private DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        initView();
        buttonInit();
        createSpotData();
    }

    private void createSpotData() throws SQLiteException {
        DB = new DBHelper(this);

        // 從Resource裡拿資訊

        // 主顧聖母堂
        String pu_chapel_name = getResources().getString(R.string.Providence_Chapel);
        String pu_chapel_info = getResources().getString(R.string.Providence_Chapel_Info);
        String pu_chapel_url = getResources().getString(R.string.Providence_Chapel_Link);

        // 主顧樓
        String pu_hall_name = getResources().getString(R.string.Providence_Hall);
        String pu_hall_info = getResources().getString(R.string.Providence_Hall_Info);
        String pu_hall_url = getResources().getString(R.string.Providence_Hall_Link);

        // 體育館
        String sport_hall_name = getResources().getString(R.string.Sport_Hall);
        String sport_hall_info = getResources().getString(R.string.Sport_hall_Info);
        String sport_hall_url = getResources().getString(R.string.Sport_Hall_Link);

        // 將資料依序存入資料庫
        DB.insertSpotData(pu_chapel_name, pu_chapel_info, pu_chapel_url);
        DB.insertSpotData(pu_hall_name, pu_hall_info, pu_hall_url);
        DB.insertSpotData(sport_hall_name, sport_hall_info, sport_hall_url);
    }

    private void beaconInit() {
        beaconManager = BeaconManager.getInstanceForApplication(this);

        //beacon AddStone m:0-3=4c000215 or alt beacon = m:2-3=0215
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        beaconManager.setForegroundBetweenScanPeriod(DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD);
        beaconManager.setForegroundScanPeriod(DEFAULT_FOREGROUND_SCAN_PERIOD);

        beaconIsRunning = true;

        beaconManager.bind(this);
    }

    private void buttonInit() {
        btnStart.setOnClickListener(v -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    beaconInit();
                }
            }).start();
        });

        btnStop.setOnClickListener(v -> {
            if (beaconIsRunning) {
                beaconManager.unbind(this);
                beaconManager.removeAllRangeNotifiers();
                beaconIsRunning = false;
            }
        });

        btn_spot01.setOnClickListener(v -> {
            spot01Enter = true;
            Intent intent = new Intent();
            intent.setClass(GuideActivity.this, GuideSpotActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("spot01", spot01Enter);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        btn_spot02.setOnClickListener(v -> {
            spot02Enter = true;
            Intent intent = new Intent();
            intent.setClass(GuideActivity.this, GuideSpotActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("spot02", spot02Enter);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        btn_spot03.setOnClickListener(v -> {
            spot03Enter = true;
            Intent intent = new Intent();
            intent.setClass(GuideActivity.this, GuideSpotActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("spot03", spot03Enter);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }

    private void initView() {
        //findView
        tvShowDisplay = findViewById(R.id.tv_Guide_information);
        btnStart = findViewById(R.id.btn_Guide_Start);
        btnStop = findViewById(R.id.btn_Guide_Stop);

        btn_spot01 = findViewById(R.id.btn01);
        btn_spot02 = findViewById(R.id.btn02);
        btn_spot03 = findViewById(R.id.btn03);

        beaconDefine = new BeaconDefine();

        // Google Map View
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.removeAllRangeNotifiers();

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @SuppressLint("SetTextI18n")
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                if (collection.size() > 0) {
                    List<Beacon> beacons = new ArrayList<>();
                    for (Beacon beaconData : collection) {
                        if (beaconData.getId1().toString().equalsIgnoreCase(IPHONE_ID) || beaconData.getId1().toString().equalsIgnoreCase(IBEACON_UUID) && beaconData.getDistance() <= 30) {
                            beacons.add(beaconData);
                        }
                    }

                    if (beacons.size() > 0) {

                        Collections.sort(beacons, new Comparator<Beacon>() {
                            @Override
                            public int compare(Beacon o1, Beacon o2) {
                                // Rssi 判斷
                                return o2.getRssi() - o1.getRssi();

                                //Distance 判斷
                                //return Double.compare(arg0.getDistance(), arg1.getDistance());
                            }
                        });

                        Beacon beacon = beacons.get(0);
                        String distance = String.valueOf(beacon.getDistance());
                        String uniqueID = String.valueOf(beacon.getId1());
                        String major = String.valueOf(beacon.getId2());
                        String minor = String.valueOf(beacon.getId3());
                        String RSSI = String.valueOf(beacon.getRssi());
                        String address = beacon.getBluetoothAddress();
                        String txPower = String.valueOf(beacon.getTxPower());

                        if (beaconDefine.getLocationMsg(major, minor).equals("IBEACON_10")) {
                            String str = String.format("主顾楼");
                            tvShowDisplay.setText(str);
                        }

                        if (beaconDefine.getLocationMsg(major, minor).equals("IBEACON_11")) {
                            String str = String.format("主顾楼");
                            tvShowDisplay.setText(str);
                        }

                        if (beaconDefine.getLocationMsg(major, minor).equals("IBEACON_12")) {
                            String str = String.format("主顾楼");
                            tvShowDisplay.setText(str);
                        }

                        @SuppressLint("DefaultLocale")
                        String str = String.format("Distance: %s%nUniqueID: %s%nMajor: %s%nMinor: %s%nRSSI: %s%nAddress: %s%nTxPower: %s%n",
                                distance, uniqueID, major, minor, RSSI, address, txPower);

//                        tvShowDisplay.setText(str);
//
//                        Log.e(TAG, str);
                    }
                }
            }
        });

        try {
            beaconManager.startRangingBeacons(new Region(IBEACON_UUID, null, null, null));
//            beaconManager.startRangingBeacons(new Region(BeaconDefine.POINT_01, null, Identifier.fromInt(87), null));
//            regionMap.put(BeaconDefine.POINT_01, 101);
//
//            beaconManager.startRangingBeacons(new Region(BeaconDefine.POINT_02, Identifier.parse(UNIQUE_ID), Identifier.fromInt(89), null));
//            regionMap.put(BeaconDefine.POINT_02, 102);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (beaconIsRunning) {
            beaconManager.unbind(this);
            beaconManager.removeAllRangeNotifiers();

            beaconIsRunning = false;
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        // Adding Marker
        LatLng pu = new LatLng(24.22619425510969, 120.57723631404022);
        gMap.addMarker(new MarkerOptions().position(pu).title("Marker at pu"));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pu,16));
    }
}