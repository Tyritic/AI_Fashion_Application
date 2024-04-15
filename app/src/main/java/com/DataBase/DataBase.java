package com.DataBase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.JavaBean.User;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class DataBase extends RoomDatabase {

    public abstract UserDao userDao();
}

