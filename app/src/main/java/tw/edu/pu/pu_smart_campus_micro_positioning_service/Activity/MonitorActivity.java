package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.imageview.ShapeableImageView;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;

public class MonitorActivity extends AppCompatActivity {

    ShapeableImageView btnBack;
    ShapeableImageView btnNextImg;

    int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        initVIew();
        initButton();
    }

    private void initButton() {
        btnBack.setOnClickListener(v -> finish());
        btnNextImg.setOnClickListener(v -> {
            if (i == 0) {
                int imageRes = getResources().getIdentifier("monitorfake" + i, "drawable", getPackageName());
                btnNextImg.setImageResource(imageRes);
                i++;
            }
            else if (i == 1) {
                int imageRes = getResources().getIdentifier("monitorfake" + i, "drawable", getPackageName());
                btnNextImg.setImageResource(imageRes);
                i = 0;
            }
        });
    }

    private void initVIew() {
        btnBack = findViewById(R.id.btn_monitor_back);
        btnNextImg = findViewById(R.id.btn_monitor_NextImg);
    }
}