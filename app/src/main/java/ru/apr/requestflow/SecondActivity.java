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

public class SecondActivity extends AppCompatActivity {
    SecondActivity.RequestFlowBroadcastReceiver broadcastReceiver;
    TextView responseTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Flow-Activity-2", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        IntentFilter filter = new IntentFilter();
        filter.addAction(RequestFlowService.BROADCAST_ACTION);
        broadcastReceiver = new SecondActivity.RequestFlowBroadcastReceiver();
        registerReceiver(broadcastReceiver, filter);
        responseTextView = findViewById(R.id.flowResult2);

        bindButton();
    }

    void bindButton(){
        Button btn = findViewById(R.id.button21);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFirstActivity();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    void setResponseText(String response) {
        Calendar s = Calendar.getInstance();
        Date time = s.getTime();
        if (responseTextView != null) {
            responseTextView.setText(String.format("[%s] Response:%s", time.toString(), response));
        }
    }

    void switchToFirstActivity(){
        Intent act1 = new Intent(this, FirstActivity.class);
        startActivity(act1);
    }

    class RequestFlowBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Flow-Receiver-2", "onReceive");

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
