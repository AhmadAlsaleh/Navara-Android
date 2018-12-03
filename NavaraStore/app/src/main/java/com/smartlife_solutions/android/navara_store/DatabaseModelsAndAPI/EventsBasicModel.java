package com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "courses")
public class EventsBasicModel implements Serializable {

    @DatabaseField(columnName = "id")
    private String id;

    @DatabaseField(columnName = "title")
    private String title;

    @DatabaseField(columnName = "description")
    private String description;

    @DatabaseField(columnName = "organization_name")
    private String organizationName;

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

    public EventsBasicModel() {

    }

    public EventsBasicModel(String id, String title, String description, String organizationName,
                            String startDate, String image, String sessionsCount,
                            String contact, String mobile) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.organizationName = organizationName;
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

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
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
