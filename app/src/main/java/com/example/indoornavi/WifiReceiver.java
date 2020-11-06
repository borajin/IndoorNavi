package com.example.indoornavi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class WifiReceiver extends BroadcastReceiver {
    private WifiManager wifiManager;
    private TableLayout map;

    int past_x;
    int past_y;

    boolean first_start = true;
    private Estimate test;

    public WifiReceiver(Context context, WifiManager wifiManager, TableLayout map) {
        this.wifiManager = wifiManager;
        this.map = map;
        test = new Estimate(context);
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            scan(context);
        } else {
            //첫 스캔이면 아무 것도 반환 안 하고 n번째 스캔이면 results 에 이전 결과가 출력됨.
            Toast.makeText(context, "측정 실패", Toast.LENGTH_SHORT).show();
        }
    }

    private void scan(Context context) {
        test.getScanList().clear();

        List<ScanResult> wifiList = wifiManager.getScanResults();
        for (ScanResult scanResult : wifiList) {
            test.addScanItem(scanResult.BSSID.replace(":", ""), scanResult.level);
        }

        String estimateResult = test.startEstimate();   // x, y 셀값

        if(estimateResult.equals("NORESULT")) {
            Toast.makeText(context, "결과 없음", Toast.LENGTH_SHORT).show();
        } else {
            if(!first_start) {
                ImageView past_cell = map.findViewById(past_y * 100 + past_x);
                past_cell.setImageResource(R.drawable.cell_fill);
            } else {
                first_start = false;
            }

            int x = Integer.parseInt(estimateResult.split(",")[0]);
            int y = Integer.parseInt(estimateResult.split(",")[1]);
            ImageView current_cell = map.findViewById(y*100+x);
            current_cell.setImageResource(R.drawable.cell_location);

            past_x = x;
            past_y = y;

            Toast.makeText(context, "측정 완료!", Toast.LENGTH_SHORT).show();
        }

        wifiManager.startScan(); //스캔 무한반복
    }
}

