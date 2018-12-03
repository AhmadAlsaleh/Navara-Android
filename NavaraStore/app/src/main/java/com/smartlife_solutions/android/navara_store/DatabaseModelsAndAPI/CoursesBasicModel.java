package com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "courses")
public class CoursesBasicModel implements Serializable {

    @DatabaseField(columnName = "id")
    private String id;

    @DatabaseField(columnName = "title")
    private String title;

    @DatabaseField(columnName = "description")
    private String description;

    @DatabaseField(columnName = "cost")
    private float cost;

    @DatabaseField(columnName = "start_date")
    private String startDate;

    @DatabaseField(columnName = "image")
    private String image;

    @DatabaseField(columnName = "sessions_count")
    private String sessionsCount;

    @DatabaseField(columnName = "contact")
    private String contact;

    @DatabaseField(columnName = "mobile")
    private String mobile;

    public CoursesBasicModel() {

    }

    public CoursesBasicModel(String id, String title, String description, float cost,
                             String startDate, String image, String sessionsCount,
                             String contact, String mobile) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.cost = cost;
        this.startDate = startDate;
        this.image = image;
        this.sessionsCount = sessionsCount;
        this.contact = contact;
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getCost() {
        return (int) cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        String date = startDate.split("T")[0];
        String timeHour = startDate.split("T")[1].split(":")[0];
        String timeMin = startDate.split("T")[1].split(":")[1];
        return date + " " + timeHour + ":" + timeMin;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSessionsCount() {
        return sessionsCount;
    }

    public void setSessionsCount(String sessionsCount) {
        this.sessionsCount = sessionsCount;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
