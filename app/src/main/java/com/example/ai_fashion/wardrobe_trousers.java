package com.example.ai_fashion;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.DB.AppDatabase;
import com.JavaBean.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class wardrobe_trousers extends AppCompatActivity
{
    private static final int PICK_IMAGE = 1;
    private Dialog mDialog;
    private static final int TAKE_PHOTO = 2;
    String user_account;
    String user_password;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wardrobe_trousers);
        ImageButton backTohomePage = findViewById(R.id.trousers_back_to_home_page);
        //获取用户账号和密码，通过上一个页面传递过来的数据
        AppDatabase DB = Room.databaseBuilder(this, AppDatabase.class,"Database")
                .allowMainThreadQueries().build();
        Intent intent1 = getIntent();
        user_account = intent1.getStringExtra("user_account");
        user_password = intent1.getStringExtra("user_password");
//        if(user_account!=null)
//        {
//            Toast.makeText(wardrobe_trousers.this,"用户名："+user_account,Toast.LENGTH_SHORT).show();
//        }
//        else
//            Toast.makeText(wardrobe_trousers.this,"用户名："+user_account, Toast.LENGTH_SHORT).show();
        user = DB.userDao().findUser(user_account,user_password);
        backTohomePage.setOnClickListener(v -> {
            Bundle bundle=new Bundle();
            Intent intent = new Intent();
            bundle.putString("user_account",user_account);
            bundle.putString("user_password",user_password);
            intent.setClass(this, Home_Page.class);
            intent.putExtra("fragment_flag", 0);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        ImageButton uploadPictures = findViewById(R.id.trousers_add_image);
        uploadPictures.setOnClickListener(v -> {
            showDialog();
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
            //Toast.makeText(wardrobe_cloth.this,"文件中有"+num,Toast.LENGTH_SHORT).show();
            //Toast.makeText(wardrobe_cloth.this,""+requestCode,Toast.LENGTH_SHORT).show();
            int num=getNum();
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
                File wardrobe = new File(user_frame, "wardrobe");
                File clothes = new File(wardrobe, "trousers");
//                if(clothes.exists())
//                {
//                    Toast.makeText(wardrobe_cloth.this,"衣柜文件夹已存在",Toast.LENGTH_SHORT).show();
//                }
                // 在这个文件夹中创建一个新的文件来保存图片
                File imageFile = new File(clothes, "trousers_"+num+".jpg");
                if(imageFile.exists())
                {
                    Toast.makeText(wardrobe_trousers.this,"上传成功",Toast.LENGTH_SHORT).show();
                }
                // 创建一个FileOutputStream来写入图片
                FileOutputStream fos = new FileOutputStream(imageFile);
                // 将Bitmap压缩为JPEG格式，并写入到FileOutputStream中
                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
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
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // 获取你之前创建的文件夹的路径
            File directory = getFilesDir();
            String user_frame_name=""+user.getUser_id();
            // 访问多级目录
            File user_frame = new File(directory, user_frame_name);
            File wardrobe = new File(user_frame, "wardrobe");
            File clothes = new File(wardrobe, "trousers");
            File imageFile = new File(clothes, "trousers_"+num+".jpg");
            try {
                // 创建一个FileOutputStream来写入图片
                FileOutputStream fos = new FileOutputStream(imageFile);

                // 将Bitmap压缩为JPEG格式，并写入到FileOutputStream中
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                Log.d("Image Save", "Image saved to " + imageFile.getAbsolutePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void showDialog() {
        if (mDialog ==null){
            initShareDialog();
        }
        mDialog.show();
    }
    private int getNum()
    {
        String user_frame_name=""+user.getUser_id();
        // 获取你之前创建的文件夹的路径
        File directory = getFilesDir();
        // 访问多级目录
        File user_frame = new File(directory, user_frame_name);
        File wardrobe = new File(user_frame, "wardrobe");
        File trousers = new File(wardrobe, "trousers");
        File[] files = trousers.listFiles();
        return files.length;
    }
}


