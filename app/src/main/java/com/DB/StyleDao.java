package com.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.JavaBean.Style;

@Dao
public interface StyleDao {
    @Insert
    void insertStyle(Style style);//插入单个风格
    @Insert
    void insertStyles(Style... styles);//插入多个风格
    @Delete
    void deleteStyle(Style style);//删除风格
    @Query("SELECT * FROM Style WHERE user_id = :user_id")
    Style findStyleByUserId(int user_id);//根据用户id查找风格
}
