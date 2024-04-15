package com.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.JavaBean.User;

@Dao
public interface UserDao {
    @Insert
    void insertUser(User user);//插入单个用户
    @Insert
    void insertUsers(User... users);//插入多个用户
    @Delete
    void deleteUser(User user);//删除用户
    @Query("SELECT * FROM Users WHERE user_account = :username AND user_password = :password")
    User findUser(String username, String password);//查找根据账号密码查找用户
    @Query("SELECT * FROM Users WHERE user_account = :username")
    User findUserByUsername(String username);//根据用户名查找用户
}