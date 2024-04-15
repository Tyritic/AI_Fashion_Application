package com.DB;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.JavaBean.Style;
import com.JavaBean.User;

@Database(entities = {User.class, Style.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract StyleDao styleDao();
    public abstract UserDao userDao();
}

