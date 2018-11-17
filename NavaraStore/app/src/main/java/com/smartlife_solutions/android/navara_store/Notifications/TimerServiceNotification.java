package com.smartlife_solutions.android.navara_store.Notifications;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.APIsURL;
import com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI.DatabaseHelper;
import com.smartlife_solutions.android.navara_store.ItemPreviewActivity;
import com.smartlife_solutions.android.navara_store.ItemsActivity;
import com.smartlife_solutions.android.navara_store.MainActivity;
import com.smartlife_solutions.android.navara_store.OfferPreviewActivity;
import com.smartlife_solutions.android.navara_store.OffersActivity;
import com.smartlife_solutions.android.navara_store.OrderInformationActivity;
import com.smartlife_solutions.android.navara_store.ProfileCartOrders;
import com.smartlife_solutions.android.navara_store.Statics;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

@SuppressLint("Registered")
public class TimerServiceNotification extends IntentService {

    public TimerServiceNotification(String name) {
        super(name);
    }

    public TimerServiceNotification() {
        super("Navara Notifications");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while (true) {
            try {
                getMyNotifications();
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e("err", e.getMessage());
            }
        }
    }

    String myToken = "";
    private void getMyNotifications() {
        DatabaseHelper db = new DatabaseHelper(this);
        try {
            myToken = db.getUserModelIntegerRuntimeException().queryForAll().get(0).getToken();
        } catch (Exception ignored) {}

        final RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, new APIsURL().getGET_NOTIFICATIONS(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        queue.cancelAll("noti");
                        Log.e("notification", s);
//                        Toast.makeText(TimerServiceNotification.this, s, Toast.LENGTH_SHORT).show();
                        try {
                            JSONArray jsonArray = new JSONArray(s);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                NotificationModel notificationModel = new NotificationModel(
                                        jsonObject.getString("id"),
                                        jsonObject.getString("subject"),
                                        jsonObject.getString("body"),
                                        jsonObject.getString("relatedTo"),
                                        jsonObject.getString("type"),
                                        jsonObject.getString("relatedID"));

                                Intent intent;

                                if (notificationModel.getRelatedTo().toLowerCase().contentEquals("item")) {
                                    if (notificationModel.getRelatedID().isEmpty()) {
                                        intent = new Intent(TimerServiceNotification.this, ItemsActivity.class);
                                    } else {
                                        intent = new Intent(TimerServiceNotification.this, ItemPreviewActivity.class)
                                                .putExtra("id", notificationModel.getRelatedID());
                                    }
                                } else if (notificationModel.getRelatedTo().toLowerCase().contentEquals("offer")) {
                                    if (notificationModel.getRelatedID().isEmpty()) {
                                        intent = new Intent(TimerServiceNotification.this, OffersActivity.class);
                                    } else {
                                        intent = new Intent(TimerServiceNotification.this, OfferPreviewActivity.class)
                                                .putExtra("id", notificationModel.getRelatedID());
                                    }
                                } else if (notificationModel.getRelatedTo().toLowerCase().contentEquals("order")) {
                                    if (notificationModel.getRelatedID().isEmpty()) {
                                        intent = new Intent(TimerServiceNotification.this, ProfileCartOrders.class)
                                                .putExtra("currentPage", 2);
                                    } else {
                                        intent = new Intent(TimerServiceNotification.this, OrderInformationActivity.class)
                                                .putExtra("id_order", notificationModel.getRelatedID());
                                    }
                                } else if (notificationModel.getRelatedTo().toLowerCase().contentEquals("cart")) {
                                    intent = new Intent(TimerServiceNotification.this, ProfileCartOrders.class)
                                            .putExtra("currentPage", 1);
                                } else if (notificationModel.getRelatedTo().toLowerCase().contentEquals("profile")) {
                                    intent = new Intent(TimerServiceNotification.this, ProfileCartOrders.class)
                                            .putExtra("currentPage", 0);
                                } else {
                                    intent = new Intent(TimerServiceNotification.this, MainActivity.class);
                                }

                                NewsNotifications.INSTANCE.notify(TimerServiceNotification.this, i,
                                        notificationModel.getSubject(), notificationModel.getBody(),
                                        notificationModel.getType(),
                                        intent, myToken);

                            }
                        } catch (Exception e) {
                            Log.e("noti ser", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                queue.cancelAll("noti");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Bearer " + myToken);
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        request.setTag("noti");
        queue.add(request);
    }

}
