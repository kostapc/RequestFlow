package ru.apr.requestflow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class FirstActivity extends AppCompatActivity {
    RequestFlowBroadcastReceiver broadcastReceiver;
    TextView responseTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Flow-Activity-1", "onCreate");
        setContentView(R.layout.activity_first);
        responseTextView = findViewById(R.id.flowResult1);

        bindButton();

        IntentFilter filter = new IntentFilter();
        filter.addAction(RequestFlowService.BROADCAST_ACTION);
        broadcastReceiver = new RequestFlowBroadcastReceiver();
        registerReceiver(broadcastReceiver, filter);

        Intent ss = new Intent(this, RequestFlowService.class);
        this.startService(ss);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    void bindButton() {
        Button btn = findViewById(R.id.button12);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToSecondActivity();
            }
        });
    }

    void switchToSecondActivity() {
        Intent act2 = new Intent(this, SecondActivity.class);
        startActivity(act2);
    }

    void setResponseText(String response) {
        Calendar s = Calendar.getInstance();
        Date time = s.getTime();
        if (responseTextView != null) {
            responseTextView.setText(String.format("[%s] Response:%s", time.toString(), response));
        }
    }

    class RequestFlowBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Flow-Receiver-1", "onReceive");

            String response;
            boolean result;
            result = intent.getBooleanExtra("success", false);
            if (result) {
                response = intent.getStringExtra("response");
            } else {
                response = intent.getStringExtra("error");
            }
            setResponseText(response);
        }
    }
}
