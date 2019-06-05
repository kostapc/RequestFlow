package ru.apr.requestflow;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import java.util.Random;

public class RequestFlowService extends Service {
    static final String TAG = "Flow-SRV";
    RequestFlowThread thread;
    String myServiceId;

    public static final String BROADCAST_ACTION = "BroadcastAction";

    public RequestFlowService() {
        myServiceId = "" + (new Random().nextInt(1000) + 1000);
        if (thread == null) {
            thread = new RequestFlowThread(myServiceId);
        }
        log("..ctor");
    }

    @Override
    public void onCreate() {
        log("onCreate");
        thread.setContext(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log("onStart-(" + thread.isAlive() + ")");
        if (!thread.isAlive()) {
            thread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void log(String message) {
        Log.d(TAG + "-" + myServiceId, message);
    }

    class RequestFlowThread extends Thread {
        static final String TAG = "Flow-TRD";

        RequestQueue queue;
        Context context;
        boolean pause_flag;
        String serviceID;
        String myThreadId;

        public RequestFlowThread(String serviceID) {
            super();
            myThreadId = "" + (new Random().nextInt(1000) + 1000);
            this.serviceID = serviceID;
            pause_flag = false;
            log("..ctor");
        }

        void setContext(Context context) {
            this.context = context;
        }

        public boolean isContextSets() {
            return this.context != null;
        }

        @Override
        public void start() {
            log("start");
            if (context == null) {
                throw new NullPointerException();
            }
            if (queue == null) {
                queue = Volley.newRequestQueue(context);
            }
            pause_flag = false;
            super.start();
        }

        @Override
        public void run() {
            log("run");
            String url = "https://postman-echo.com/get?foo1=bar1&foo2=bar2";
            while (!pause_flag) {
                log(".");
                StringRequest request = getStringRequest(url);
                queue.add(request);

                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void pause() {
            pause_flag = true;
        }

        private StringRequest getStringRequest(String url) {
            return new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            successResponse(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            errorResponse(error);
                        }
                    }
            );
        }

        private void successResponse(String response) {
            log("Success response:" + response);

            Intent i = new Intent();
            i.setAction(BROADCAST_ACTION);
            i.putExtra("success", true);
            i.putExtra("response", response);
            sendBroadcast(i);
        }

        private void errorResponse(VolleyError error) {
            log("Error response:" + error.networkResponse.statusCode);

            Intent i = new Intent();
            i.setAction(BROADCAST_ACTION);
            i.putExtra("success", false);
            i.putExtra("error", error.networkResponse.statusCode);
            sendBroadcast(i);
        }

        void log(String message) {
            Log.d(TAG + "-" + getID(), message);
        }

        String getID() {
            return serviceID + "-" + myThreadId + "(" + getId() + ")";
        }
    }
}
