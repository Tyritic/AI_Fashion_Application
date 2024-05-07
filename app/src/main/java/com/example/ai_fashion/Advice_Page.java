package com.example.ai_fashion;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.Utils.ImageProcessor;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Advice_Page extends AppCompatActivity {

    //初始化组件
    ImageButton back;
    ImageButton add_image;
    ImageView user_image;
    EditText advice_text;
    String user_account;
    String user_password;
    String src_advice;
    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;
    private Dialog mDialog;
    private final String translateUrl = "http://api.niutrans.com/NiuTransServer/translation?";
    private String serverUrl = "https://ad97-58-82-220-12.ngrok-free.app/upload-fullimage";
    public static final int REQUSET_CAMERA_PERMISSION  = 5555;
    public static final int REQUSET_STORAGE_PERMISSION = 6666;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_advice_page);

        Intent intent1 = getIntent();
        user_account = intent1.getStringExtra("user_account");
        user_password = intent1.getStringExtra("user_password");
        if(user_account==null||user_password==null)
        {
            Toast.makeText(Advice_Page.this,"Account_Page接收失败",Toast.LENGTH_SHORT).show();
        }


        //检查是否具有相机权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，请求存储权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUSET_CAMERA_PERMISSION);
        }
        EdgeToEdge.enable(this);

        //检查是否具有存储权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，请求存储权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUSET_STORAGE_PERMISSION);
        }

        //绑定组件
        back = findViewById(R.id.back);
        add_image = findViewById(R.id.add_image);
        user_image = findViewById(R.id.user_image);
        advice_text = findViewById(R.id.chat_box);

        //返回按钮
        back.setOnClickListener(v -> {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("user_account", user_account);
            bundle.putString("user_password", user_password);
            intent.putExtras(bundle);
            intent.setClass(this, Home_Page.class);
            intent.putExtra("fragment_flag", 1);
            startActivity(intent);
        });

        //添加图片按钮
        add_image.setOnClickListener(v -> {
            showDialog();
        });
    }

    //初始化分享对话框
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

    //获取图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null)
        {
            // 获取图片的 Uri
            Uri selectedImage = data.getData();
            ImageProcessor imageProcessor = new ImageProcessor();
            // 根据图片的 Uri 转换为 Base64 编码的字符串
            String jsonString=imageProcessor.encodeImageUriToBase64(this,selectedImage);

            // 将 Base64 编码的字符串存储在 Map 中，设置键值对
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("image_data", jsonString); // 将 encodedImage 字符串存储在 "image_data" 键下
            Gson gson = new Gson();
            String json = gson.toJson(dataMap);
            System.out.println(json);
            user_image.setImageURI(selectedImage);
            add_image.setVisibility(View.INVISIBLE);
            imageProcessor. uploadTextAsync(serverUrl, json, new ImageProcessor.TextProcessorListener() {
                @Override
                public void onUploadSuccess(String json) {
                    try {
                        // 解析返回的 JSON 字符串
                        JSONObject jsonObject = new JSONObject(json);
                        src_advice = jsonObject.getString("advice");
                        System.out.println(src_advice);
                        new Thread(() -> {
                            try {
                                String advice = getTranslate(src_advice);
                                //更新UI
                                runOnUiThread(() -> {
                                    user_image.setImageURI(selectedImage);
                                    add_image.setVisibility(View.INVISIBLE);
                                    advice_text.setText(src_advice);
                                });
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }).start();

                    } catch (JSONException e) {
                        System.out.println("解析失败");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onUploadFailure(Exception e) {
                    Log.e("Advice_Page", "上传失败", e);
                }
            });

        }
        else if(requestCode == TAKE_PHOTO && resultCode == RESULT_OK && data != null)
        {
            Bundle extras = data.getExtras();// 获取拍摄的图片
            // 将图片转换为Base64编码的字符串
            Bitmap imageBitmap;
            if (extras != null) {
                imageBitmap = (Bitmap) extras.get("data");
            } else {
                imageBitmap = null;
            }
            ImageProcessor imageProcessor = new ImageProcessor();
            // 将图片转换为Base64编码的字符串
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream); // 使用PNG格式，100表示无压缩
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            // 将Bitmap转换为Base64字符串
            String encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT);

            // 将 Base64 编码的字符串存储在 Map 中，构成键值对
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("image_data", encodedString); // 将 encodedImage 字符串存储在 "image_data" 键下
            Gson gson = new Gson();
            String jsonstring = gson.toJson(dataMap);

            imageProcessor.uploadTextAsync(serverUrl, jsonstring, new ImageProcessor.TextProcessorListener() {
                @Override
                public void onUploadSuccess(String json) {
                    try {
                        // 解析返回的 JSON 字符串
                        JSONObject jsonObject = new JSONObject(json);
                        String advice = jsonObject.getString("advice");
                        System.out.println(advice);
                        runOnUiThread(() -> {
                            user_image.setImageBitmap(imageBitmap);
                            add_image.setVisibility(View.INVISIBLE);
                            advice_text.setText(advice);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onUploadFailure(Exception e) {
                    Log.e("Advice_Page", "上传失败", e);
                }
            });
        }


    }

    private void showDialog() {
        if (mDialog ==null){
            initShareDialog();
        }
        mDialog.show();
    }


    //调用翻译接口
    public String getTranslate (String input) throws UnsupportedEncodingException {
        String urlString = translateUrl + "from=en&to=zh&apikey="+"8fa7ca2fa4e0d5e47642b7eb325a572c"+"&src_text="+ URLEncoder.encode(input,"utf-8");
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            if (connection.getResponseCode() == 200)
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                // 解析返回的 JSON 字符串
                JSONObject jsonObject = new JSONObject(response.toString());
                String result = jsonObject.getString("tgt_text");
                return result;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}