package com.JavaBean;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Users")
public class User {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int user_id;
    @ColumnInfo(name = "user_nickname")
    private String user_nickname;
    @ColumnInfo(name = "user_account")
    private String user_account;
    @ColumnInfo(name = "user_password")
    private String user_password;
    @ColumnInfo(name = "user_birthday")
    private String user_birthday;
    @ColumnInfo(name = "user_gender")
    private String user_gender;

    public double getUser_height() {
        return user_height;
    }

    public void setUser_height(double user_height) {
        this.user_height = user_height;
    }

    public double getUser_weight() {
        return user_weight;
    }

    public void setUser_weight(double user_weight) {
        this.user_weight = user_weight;
    }

    public double getUser_proportion() {
        return user_proportion;
    }

    public void setUser_proportion(double user_proportion) {
        this.user_proportion = user_proportion;
    }

    @ColumnInfo(name = "user_height")
    private double user_height;

    @ColumnInfo(name = "user_weight")
    private double user_weight;

    @ColumnInfo(name="user_proportion")
    private double user_proportion;
    public String getUser_birthday() {
        return user_birthday;
    }

    public User() {
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public void setUser_birthday(String user_birthday) {
        this.user_birthday = user_birthday;
    }



    public String getUser_gender() {
        return user_gender;
    }

    public void setUser_gender(String user_gender) {
        this.user_gender = user_gender;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public User(String user_nickname,String user_account, String user_password, String user_birthday, String user_gender) {
        this.user_account = user_account;
        this.user_password = user_password;
        this.user_nickname = user_nickname;
        this.user_birthday = user_birthday;
        this.user_gender=user_gender;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_account() {
        return user_account;
    }

    public void setUser_account(String user_account) {
        this.user_account = user_account;
    }
}
