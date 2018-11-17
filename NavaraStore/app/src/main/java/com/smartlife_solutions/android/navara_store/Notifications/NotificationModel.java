package com.smartlife_solutions.android.navara_store.Notifications;

import android.os.Bundle;
import android.util.Log;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationModel {

    private String id, subject, body, relatedTo, type, relatedID;

    public NotificationModel() {
        this.id = "";
        this.subject = "";
        this.body = "";
        this.relatedTo = "";
        this.type = "";
        this.relatedID = "";
    }

    public NotificationModel(String id, String subject, String body, String relatedTo, String type, String relatedID) {
        this.id = id;
        this.subject = subject;
        this.body = body;
        this.relatedTo = relatedTo;
        this.type = type;
        this.relatedID = relatedID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getRelatedTo() {
        return relatedTo;
    }

    public void setRelatedTo(String relatedTo) {
        this.relatedTo = relatedTo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRelatedID() {
        if (relatedID.equals("null")) {
            return "";
        }
        return relatedID;
    }

    public void setRelatedID(String relatedID) {
        this.relatedID = relatedID;
    }

    public void showNotification(int resultCode, Bundle resultData) {

        try {
            JSONObject jsonObject = new JSONObject(resultData.getString("notification"));
            NotificationModel notificationModel = new NotificationModel(
                    jsonObject.getString("id"),
                    jsonObject.getString("subject"),
                    jsonObject.getString("body"),
                    jsonObject.getString("relatedTo"),
                    jsonObject.getString("type"),
                    jsonObject.getString("relatedID"));

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("err", e.getMessage());
        }
    }
}
