package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.ApiConnect.VolleyApi;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon.BeaconController;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon.BeaconStore;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.DefaultSetting;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.YuuzuAlertDialog;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.RequestHelper;

public class SafetyActivity extends AppCompatActivity {

    private final String TAG = "SafetyActivity: ";

    private boolean animationRunning = false;
    private boolean sosIsRunning = false;
    private boolean firstChecked = true;
    private boolean apiChecked = true;

    private int count_Animation = 0;
    private int count_Api = 0;
    private int count_Alert = 0;
    private int emergency = 0;

    LottieAnimationView animation;
    MaterialTextView btnSafety;
    ShapeableImageView btnBack, btnSOS;

    RequestHelper requestHelper;
    BeaconStore beaconStore;
    BeaconController beaconController;
    YuuzuAlertDialog alertDialog;

    MediaPlayer mediaPlayer;
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety);

        initView();
        requestHelper.requestBluetooth();
        beaconController.initBeacon();
        initButton();
    }

    private void initButton() {


        btnBack.setOnClickListener(v -> {
            animationStop();
            beaconController.stopScanning();
            finish();
        });

        btnSafety.setOnClickListener(v -> {
            if (!animationRunning) {
                alertDialog.showDialog("安全監控", "啓動此功能會實時偵測您所在的位置", new YuuzuAlertDialog.AlertCallback() {
                    @Override
                    public void onOkay(DialogInterface dialog, int which) {
                        animationStart();
                        emergency = 1;
                        startScanning();
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancel(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

            } else {
                beaconController.stopScanning();
                sos_Stop();
                animationStop();
                apiSafetyStop();
            }
        });

        btnSOS.setOnClickListener(v -> {
            if (animationRunning) {
                if (!sosIsRunning) {
                    alertDialog.showDialog("安全通道SOS", "此功能會直接呼叫警衛室", new YuuzuAlertDialog.AlertCallback() {
                        @Override
                        public void onOkay(DialogInterface dialog, int which) {
                            emergency = 2;
                            apiChecked = true;
                            sos_Start();
                            dialog.dismiss();
                        }

                        @Override
                        public void onCancel(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                } else {
                    sos_Stop();
                    apiSosStop();
                    emergency = 1;
                    apiChecked = true;
                }
            } else {
                alertDialog.showDialog("安全通道SOS", "請麻煩先打開安全監控才能打開SOS!", new YuuzuAlertDialog.AlertCallback() {
                    @Override
                    public void onOkay(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancel(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void initView() {
        btnSOS = findViewById(R.id.btn_safety_SOS);
        btnBack = findViewById(R.id.btn_safety_back);
        btnSafety = findViewById(R.id.btn_safety_trace);
        animation = findViewById(R.id.safety_Animation);

        btnSafety.setText(R.string.safety_Start);

        beaconController = new BeaconController(this);
        beaconStore = new BeaconStore(this);
        requestHelper = new RequestHelper(this);
        alertDialog = new YuuzuAlertDialog(this);
    }

    private void startScanning() {
        Log.e(TAG, "StartScanning...");
        beaconController.startScanning((beacons, region) -> {
            if (beacons.size() > 0) {
                Log.e(TAG, "Get Data Successfully!");
                count_Alert = 0;
                firstChecked = false;
                SafetyActivity.this.apiTimer();

                List<Beacon> list = new ArrayList<>(beacons);
                Collections.sort(list, (o1, o2) -> Double.compare(o2.getDistance(), o1.getDistance()));
                beaconStore.beaconData(apiChecked, list.get(0), sendAPI(emergency));

            } else {
                Log.e(TAG, "No Beacon Here.");

                if (firstChecked) {
                    Log.e(TAG, "Alert! 此道路暫時不支援安全通道。");
                    count_Animation++;
                    SafetyActivity.this.runOnUiThread(() -> {
                        if (count_Animation == 3) {
                            alertDialog.showDialog("安全通道", "此道路暫時不支援安全通道!", new YuuzuAlertDialog.AlertCallback() {
                                @Override
                                public void onOkay(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }

                                @Override
                                public void onCancel(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            SafetyActivity.this.animationStop();
                            SafetyActivity.this.sos_Stop();
                            beaconController.stopScanning();
                            count_Animation = 0;
                        }
                    });

                } else {
                    SafetyActivity.this.dialogTimer();
                }
            }
        });
    }

    private String sendAPI(int emergency) {
        String url = "";
        if (emergency == 1)
            url = DefaultSetting.API_SAFETY_MONITOR_START;
        else if (emergency == 2)
            url = DefaultSetting.API_SAFETY_SOS_START;
        return url;
    }

    private void animationStart() {
        animation.playAnimation();
        btnSafety.setText(R.string.safety_Activate);

        animationRunning = true;
    }

    private void animationStop() {
        animation.setProgress(0);
        animation.cancelAnimation();
        btnSafety.setText(R.string.safety_Start);

        animationRunning = false;
    }

    private void sos_Start() {
        soundPlay();
        int imageRes = getResources().getIdentifier("sos_1", "drawable", getPackageName());
        btnSOS.setImageResource(imageRes);
        sosIsRunning = true;
    }

    private void sos_Stop() {
        soundStop();
        int imageRes = getResources().getIdentifier("sos_0", "drawable", getPackageName());
        btnSOS.setImageResource(imageRes);
        sosIsRunning = false;
    }

    private void soundPlay() {
        try {
            audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

            if (audioManager.isSpeakerphoneOn()) {
                audioManager.setStreamVolume(AudioManager.STREAM_RING, 100, 0);

                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.beepsoundeffect);
                }

                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> mediaPlayer.start());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void soundStop() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void apiSafetyStop() {
        VolleyApi volleyApi = new VolleyApi(this, DefaultSetting.API_SAFETY_MONITOR_END);
        volleyApi.post_API_Safety_Stop();
    }

    private void apiSosStop() {
        VolleyApi volleyApi = new VolleyApi(this, DefaultSetting.API_SAFETY_SOS_END);
        volleyApi.post_API_Safety_Stop();
    }

    private void apiTimer() {
        new Handler().postDelayed(new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "count_API: " + count_Api);
                count_Api++;
                if (count_Api == 5) {
                    apiChecked = true;
                    count_Api = 0;

                } else {
                    apiChecked = false;
                }
            }
        }, 1000);
    }

    private void dialogTimer() {
        new Handler().postDelayed(() -> {
            Log.e(TAG, "count_Alert: " + count_Alert);
            count_Alert++;
            if (count_Alert >= 59) {
                count_Alert = 0;
                Log.e(TAG, "Alert!!!!!");
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconController.stopScanning();
        apiSosStop();
        apiSafetyStop();
    }
}