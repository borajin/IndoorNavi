package com.example.indoornavi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    ImageButton speechStartBtn;
    STT stt;
    private MyBroadCastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initUi();
    }

    //마지막 초기화 작업?? onresume은 activity가 전면에 나타날 때, oncreate 호출 이후에도 호출됨.
    @Override
    public void onResume() {
        super.onResume();
        receiver = new MyBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("navigation-start");
        registerReceiver(receiver, intentFilter);
    }

    //onStop, onDestroy 호출되기 이전에 호출됨. onresume 쌍으로 보고 거기서 했던 작업을 여기서 정리, 멈춤.
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private void init() {
        //GPS 켜져있는지 검사..
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            ToastMsg("gps를 켜주세요...");
            finish();
        } else {
            checkNetwork();
            checkDB();
        }
    }

    private void initUi() {
        speechStartBtn =  findViewById(R.id.speechBtn);
        stt = new STT(this);

        speechStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stt.startSTT();
            }
        });
    }

    private void checkNetwork() {
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                //쓰리지나 LTE로 연결됐다면
                turnOnWifi();
            }
        } else {
            turnOnWifi();
        }
    }

    private void turnOnWifi() {
        ToastMsg("wifi를 킵니다.");
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(this.WIFI_SERVICE);
        wifi.setWifiEnabled(true);
    }

    private void checkDB() {
        String DB_PATH = "/data/data/" + getPackageName() + "/databases";
        String DB_NAME = "/test.db";
        File target = new File(DB_PATH + DB_NAME);

        if (!target.exists() || target.length() <= 0) {
            ToastMsg("db파일을 다운로드 합니다.");
            String DB_DOWNLOAD_URL = "https://drive.google.com/uc?export=download&id=1SmLb-kHpQQnZSP45bDJFDI2GjFM11EA8";
            new DBdownload(this).execute(DB_DOWNLOAD_URL, DB_PATH, DB_NAME);
        } else {
            ToastMsg("db파일 있음");
        }
    }

    private void ToastMsg(final String msg) {
        final String message = msg;
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        stt.endSTT();
        super.onDestroy();
    }
}