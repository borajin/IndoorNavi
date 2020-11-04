package com.example.indoornavi;

import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NaviActivity extends AppCompatActivity {
    private TableLayout map;
    private WifiManager wifiManager;
    private WifiReceiver receiverWifi;
    private Button naviEndBtn;
    private TTS tts;
    private TextView routeTxt;
    private TextView directionTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);

        map = findViewById(R.id.map);
        routeTxt = findViewById(R.id.routeTxt);
        directionTxt = findViewById(R.id.directionTxt);
        drawMap();

        tts = new TTS(this);

        naviEndBtn = findViewById(R.id.naviEndBtn);
        naviEndBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts.startTTS("안내를 종료합니다.");
                finish();
            }
        });


        //wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //wifiManager.startScan();

        Directions d = new Directions(this);
        String route = d.getRoute("1번출구", "4번출구");
        routeTxt.setText(route);

        String direction = d.getDirection(3, 2, "3번출구");
        directionTxt.setText(direction);
    }

    //마지막 초기화 작업?? onresume은 activity가 전면에 나타날 때, oncreate 호출 이후에도 호출됨.
    @Override
    public void onResume() {
        super.onResume();
        receiverWifi = new WifiReceiver(this, wifiManager, map);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(receiverWifi, intentFilter);
    }

    //onStop, onDestroy 호출되기 이전에 호출됨. onresume 쌍으로 보고 거기서 했던 작업을 여기서 정리, 멈춤.
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiverWifi);
    }

    private void drawMap() {
        int[][] mapXY = new int[76][8];

        DBAdapter db = new DBAdapter(this);
        String sql = "select CELL_X, CELL_Y from TR_FPDB_MAP;";
        Cursor cursor = db.search(sql);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int cell_x = cursor.getInt(cursor.getColumnIndex("CELL_X"));
                int cell_y = cursor.getInt(cursor.getColumnIndex("CELL_Y"));

                mapXY[cell_y][cell_x] = 1;
            }
        }
        cursor.close();

        //db 닫기
        try {
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i <= 75; i++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            for (int j = 0; j <= 7; j++) {
                ImageView cell = new ImageView(this);
                cell.setId((i*100) + j);

                if(mapXY[i][j] == 1) {
                    cell.setImageResource(R.drawable.cell_fill);
                } else {
                    cell.setImageResource(R.drawable.cell_blank);
                }

                cell.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                row.addView(cell);
            }

            map.addView(row);
        }
    }
}
