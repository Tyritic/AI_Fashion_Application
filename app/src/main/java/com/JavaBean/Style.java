package com.JavaBean;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Style",
        foreignKeys = @ForeignKey
                (entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "user_id",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE))
public class Style {
    //将user_id设置为外键
    //将style_id设置为主键
    @NonNull
    @ColumnInfo(name = "user_id")
    private int user_id;
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int style_id;
    @ColumnInfo(name="user_cloth")
    private String user_cloth;

    public Style(int user_id, String user_cloth, String user_trousers, String user_shoes) {
        this.user_id = user_id;
        this.user_cloth = user_cloth;
        this.user_trousers = user_trousers;
        this.user_shoes = user_shoes;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getStyle_id() {
        return style_id;
    }

    public void setStyle_id(int style_id) {
        this.style_id = style_id;
    }

    public String getUser_cloth() {
        return user_cloth;
    }

    public void setUser_cloth(String user_cloth) {
        this.user_cloth = user_cloth;
    }

    public String getUser_trousers() {
        return user_trousers;
    }

    public void setUser_trousers(String user_trousers) {
        this.user_trousers = user_trousers;
    }

    public String getUser_shoes() {
        return user_shoes;
    }

    public void setUser_shoes(String user_shoes) {
        this.user_shoes = user_shoes;
    }

    @ColumnInfo(name="user_trousers")
    private String user_trousers;

    @ColumnInfo(name="user_shoes")
    private String user_shoes;

}