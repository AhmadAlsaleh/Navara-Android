package com.smartlife_solutions.android.navara_store.DatabaseModelsAndAPI;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "user")
public class UserModel {

    @DatabaseField(columnName = "user_id")
    private String UserID;

    @DatabaseField(columnName = "username")
    private String Username;

    @DatabaseField(columnName = "email")
    private String Email;

    @DatabaseField(columnName = "phone")
    private String PhoneNumber;

    @DatabaseField(columnName = "first_name")
    private String FirstName;

    @DatabaseField(columnName = "token")
    private String token;

    @DatabaseField(columnName = "password")
    private String Password;

    public UserModel() {

    }

    public UserModel(String userID, String username, String email, String phoneNumber, String firstName, String token, String password) {
        this.UserID = userID;
        this.Username = username;
        this.Email = email;
        this.PhoneNumber = phoneNumber;
        this.FirstName = firstName;
        this.token= token;
        this.Password = password;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}