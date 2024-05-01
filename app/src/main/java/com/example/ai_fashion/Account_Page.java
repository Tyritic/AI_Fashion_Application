package com.example.ai_fashion;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Insert;
import androidx.room.Room;

import com.DB.AppDatabase;
import com.JavaBean.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Account_Page extends AppCompatActivity {
    ImageButton backTohomePage;
    EditText account_input;
    EditText password_input;
    EditText birthday_input;
    EditText nickname_input;
    String user_account;
    String user_password;

    String account;
    String password;
    String birthday;
    String nickname;
    String newAccount;
    String newPassword;
    User user;
    CircleImage circleImage;
    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;
    public static final int REQUSET_CAMERA_PERMISSION  = 5555;
    private Dialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_page);
        //检查是否具有存储权限
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，请求存储权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUSET_CAMERA_PERMISSION);
        }
        //获取用户账号和密码，通过上一个页面传递过来的数据
        Intent intent1 = getIntent();
        user_account = intent1.getStringExtra("user_account");
        user_password = intent1.getStringExtra("user_password");
        if(user_account==null||user_password==null)
        {
            Toast.makeText(Account_Page.this,"Account_Page接收失败",Toast.LENGTH_SHORT).show();
        }
        else if(user_account!=null&&user_password!=null)
        {
            //Toast.makeText(Account_Page.this,"Account_Fragment接收成功",Toast.LENGTH_SHORT).show();
        }
        AppDatabase DB = Room.databaseBuilder(this, AppDatabase.class,"Database")
                .allowMainThreadQueries().build();
        user = DB.userDao().findUser(user_account,user_password);
        //user_icon=user.getUser_icon();
        final String user_icon=user.getUser_icon();
        account_input = findViewById(R.id.account_input);
        password_input = findViewById(R.id.password_input);
        birthday_input = findViewById(R.id.birthday_input);
        nickname_input = findViewById(R.id.nickname_input);
        //设置输入框的默认值为用户的信息
        account_input.setText(user.getUser_account());
        password_input.setText(user.getUser_password());
        birthday_input.setText(user.getUser_age());
        nickname_input.setText(user.getUser_nickname());
        Button modify = findViewById(R.id.modify_button);
        circleImage = findViewById(R.id.head_image);
        //设置头像
        circleImage.setClickable(true);
        circleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        Bitmap bitmap = BitmapFactory.decodeFile(user.getUser_icon());
        circleImage.setImageBitmap(bitmap);
        //修改按钮点击事件
        modify.setOnClickListener(v -> {
            //获取用户输入的信息
            account = account_input.getText().toString();
            password = password_input.getText().toString();
            birthday = birthday_input.getText().toString();
            nickname= nickname_input.getText().toString();
            //判断用户是否修改了信息
            boolean not_modified=account.equals(user_account)&&password.equals(user_password)&&birthday.equals(user.getUser_age())&&nickname.equals(user.getUser_nickname());
            if(not_modified)
            {
                Toast.makeText(Account_Page.this,"未修改",Toast.LENGTH_SHORT).show();
            }
            else if(!account.equals(user_account)&&DB.userDao().findUserByUseraccount(account)!=null)
            {
                Toast.makeText(Account_Page.this,"用户名已存在",Toast.LENGTH_SHORT).show();
            }
            else
            {
//                User user1 = new User();
//                user1.setUser_account(account);
//                user1.setUser_password(password);
//                user1.setUser_age(birthday);
//                user1.setUser_nickname(nickname);
//                user1.setUser_id(user.getUser_id());
//                user1.setUser_gender(user.getUser_gender());
//                user1.setUser_height(user.getUser_height());
//                user1.setUser_weight(user.getUser_weight());
//                user1.setUser_proportion(user.getUser_proportion());
//                user1.setUser_icon(user.getUser_icon());
//                DB.userDao().updateUser(user1);
                user.setUser_account(account);
                user.setUser_password(password);
                user.setUser_age(birthday);
                user.setUser_nickname(nickname);
                DB.userDao().updateUser(user);
                Toast.makeText(Account_Page.this,"修改成功",Toast.LENGTH_SHORT).show();
            }
        });
        backTohomePage = findViewById(R.id.back_to_home_page);
        //返回按钮点击事件
        backTohomePage.setOnClickListener(v -> {
                //获取用户退出后的账号和密码
                newAccount=account_input.getText().toString();
                newPassword=account_input.getText().toString();
                //将用户账号和密码包装成传递
                Bundle bundle=new Bundle();
                bundle.putString("user_account",newAccount);
                bundle.putString("user_password",newPassword);
                if(newPassword==null||newAccount==null)
                {
                    Toast.makeText(Account_Page.this,"Account_Page发送失败",Toast.LENGTH_SHORT).show();
                }
                else if(newPassword!=null&&newAccount!=null)
                {
                    //Toast.makeText(Account_Page.this,"Account_Page发送成功",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(this, Home_Page.class);
                    intent.putExtra("fragment_flag", 2);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            });

    }

    private void initShareDialog() {
        mDialog = new Dialog(this, R.style.dialogStyle);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setCancelable(true);            //点击框外，框退出
        Window window = mDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);      //位于底部
        //window.setWindowAnimations(R.style.dialog_share);    //弹出动画
        View inflate = View.inflate(this, R.layout.dialog_retrieve_password, null);
        //退出按钮
        inflate.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog != null && mDialog.isShowing()){
                    mDialog.dismiss();      //消失，退出
                }
            }
        });
        inflate.findViewById(R.id.dialog_photo_library).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用本地相册
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);    //访问本地相册
                //访问选中的图片
                if (mDialog != null && mDialog.isShowing()){
                    mDialog.dismiss();      //消失，退出
                }
            }
        });
        inflate.findViewById(R.id.dialog_take_photos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用相机
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, TAKE_PHOTO);    //访问相机
                //访问拍摄的图片
                if (mDialog != null && mDialog.isShowing()){
                    mDialog.dismiss();      //消失，退出
                }
            }
        });
        window.setContentView(inflate);
        //横向充满
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null)
        {
            int num=getNum();
            //Toast.makeText(wardrobe_cloth.this,"文件中有"+num,Toast.LENGTH_SHORT).show();
            //Toast.makeText(wardrobe_cloth.this,""+requestCode,Toast.LENGTH_SHORT).show();
            Uri selectedImage = data.getData();
            try {
                // 获取图片的输入流
                InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                // 将输入流解码为Bitmap
                Bitmap selectedBitmap = BitmapFactory.decodeStream(imageStream);
                String user_frame_name=""+user.getUser_id();
                // 获取你之前创建的文件夹的路径
                File directory = getFilesDir();
                // 访问多级目录
                File user_frame = new File(directory, user_frame_name);
                File icon = new File(user_frame, "icon");
//                if(clothes.exists())
//                {
//                    Toast.makeText(wardrobe_cloth.this,"衣柜文件夹已存在",Toast.LENGTH_SHORT).show();
//                }
                // 在这个文件夹中创建一个新的文件来保存图片
                File imageFile = new File(icon, "icon_"+num+".jpg");
                if(imageFile.exists())
                {
                    Toast.makeText(Account_Page.this,"上传成功",Toast.LENGTH_SHORT).show();
                }
                // 创建一个FileOutputStream来写入图片
                FileOutputStream fos = new FileOutputStream(imageFile);
                // 将Bitmap压缩为JPEG格式，并写入到FileOutputStream中
                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                String user_icon=imageFile.getAbsolutePath();
                circleImage.setImageBitmap(selectedBitmap);
                AppDatabase DB = Room.databaseBuilder(this, AppDatabase.class,"Database")
                        .allowMainThreadQueries().build();
//                String account = account_input.getText().toString();
//                String password = password_input.getText().toString();
//                User user = DB.userDao().findUser(account,password);
//                User user1 = new User();
//                user1.setUser_account(account);
//                user1.setUser_password(password);
//                user1.setUser_age(user.getUser_age());
//                user1.setUser_nickname(user.getUser_nickname());
//                user1.setUser_id(user.getUser_id());
//                user1.setUser_height(user.getUser_height());
//                user1.setUser_weight(user.getUser_weight());
//                user1.setUser_proportion(user.getUser_proportion());
//                user1.setUser_icon(user_icon);
//                user1.setUser_gender(user.getUser_gender());
//                DB.userDao().updateUser(user1);
                user.setUser_icon(user_icon);
                DB.userDao().updateUser(user);
                Log.d("Image Save", "Image saved to " + imageFile.getAbsolutePath());
                //Toast.makeText(wardrobe_cloth.this,"第"+i+"张",Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(requestCode == TAKE_PHOTO && resultCode == RESULT_OK && data != null)
        {
            //Toast.makeText(wardrobe_cloth.this,"文件中有"+num,Toast.LENGTH_SHORT).show();
            //Toast.makeText(wardrobe_cloth.this,""+requestCode,Toast.LENGTH_SHORT).show();
            int num=getNum();
            //Log.d("Image Save", "Image saved to " + num);
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // 获取你之前创建的文件夹的路径
            File directory = getFilesDir();
            String user_frame_name=""+user.getUser_id();
            // 访问多级目录
            File user_frame = new File(directory, user_frame_name);
            File icon = new File(user_frame, "icon");
            File imageFile = new File(icon, "icon_"+num+".jpg");
            try {
                // 创建一个FileOutputStream来写入图片
                FileOutputStream fos = new FileOutputStream(imageFile);

                // 将Bitmap压缩为JPEG格式，并写入到FileOutputStream中
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                String user_icon=imageFile.getAbsolutePath();
                circleImage.setImageBitmap(imageBitmap);
                AppDatabase DB = Room.databaseBuilder(this, AppDatabase.class,"Database")
                        .allowMainThreadQueries().build();
//                String account = account_input.getText().toString();
//                String password = password_input.getText().toString();
//                User user = DB.userDao().findUser(account,password);
//                User user1 = new User();
//                user1.setUser_account(account);
//                user1.setUser_password(password);
//                user1.setUser_age(user.getUser_age());
//                user1.setUser_nickname(user.getUser_nickname());
//                user1.setUser_id(user.getUser_id());
//                user1.setUser_height(user.getUser_height());
//                user1.setUser_weight(user.getUser_weight());
//                user1.setUser_proportion(user.getUser_proportion());
//                user1.setUser_icon(user_icon);
//                user1.setUser_gender(user.getUser_gender());
//                DB.userDao().updateUser(user1);
                user.setUser_icon(user_icon);
                DB.userDao().updateUser(user);
                Log.d("Image Save", "Image saved to " + imageFile.getAbsolutePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //上传图片后同步到RecyclerView
    }
    private int getNum()
    {
        String user_frame_name=""+user.getUser_id();
        // 获取你之前创建的文件夹的路径
        File directory = getFilesDir();
        // 访问多级目录
        File user_frame = new File(directory, user_frame_name);
        File icon = new File(user_frame, "icon");
        File[] files = icon.listFiles();
        return files.length;
    }

    private void showDialog() {
        if (mDialog ==null){
            initShareDialog();
        }
        mDialog.show();
    }

}