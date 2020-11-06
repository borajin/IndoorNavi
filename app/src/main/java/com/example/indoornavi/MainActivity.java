package com.example.indoornavi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/*
DB 다운로드
메뉴화면
 */

public class MainActivity extends AppCompatActivity {

    private ImageButton speechStartBtn;
    private Speech speech;
    private SpeechReceiver receiver;

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
        receiver = new SpeechReceiver();
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
        speech = new Speech(this);

        speechStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speech.litsen();
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
            String id = "1pyQpKY44IHs4JU4rkA0NdbUuOmE0cBRb";
            String DB_DOWNLOAD_URL = "https://docs.google.com/uc?id=" + id + "&export=download";
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
        speech.endSpeech();
        super.onDestroy();
    }
}

class DBdownload extends AsyncTask<String, String, String> {

    private ProgressDialog mDlg;
    private Context context;

    public DBdownload(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        mDlg = new ProgressDialog(context);
        mDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDlg.setMessage("테스트_장전역 DB 다운로드...");
        mDlg.show();

        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            Thread.sleep(100);

            //상위 폴더 없으면 만들기
            String DB_PATH = params[1];
            String DB_NAME = params[2];
            File dir = new File(DB_PATH);
            if(!dir.exists()) dir.mkdirs();

            File target = new File(dir + DB_NAME);
            target.createNewFile();


            //db 다운로드
            URL url = new URL(params[0]);
            URLConnection connection = url.openConnection();

            InputStream is = connection.getInputStream();
            FileOutputStream fos = new FileOutputStream(target);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            byte[] buf = new byte[1024];
            int count;
            while ((count = is.read(buf)) > 0) {
                bos.write(buf, 0, count);
            }

            bos.close();
            fos.close();
            is.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onPostExecute(String unused) {
        mDlg.dismiss();
    }
}
