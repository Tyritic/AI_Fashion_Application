package com.Utils;
import androidx.annotation.NonNull;
import okhttp3.*;

import java.io.FileOutputStream;
import java.io.IOException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
public class ImageProcessor {
    Bitmap bitmap;
    private static final String TAG = "MyActivity";

    private final OkHttpClient client;

    public ImageProcessor() {
        client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        /*
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
                client = new OkHttpClient();
         */
    }

    //定义异步上传图片方法，传参：目标Url，图片字节数组，监听器（用于在异步图像上传任务的不同阶段提供通知或回调）
    public void uploadImageAsync(String url, String jsonString,  ImageProcessorListener listener) {
        //1MediaType mediaType = MediaType.get("application/json"); // 注意这里我们没有指定字符集
        Log.d(TAG,  "re"+jsonString);
        MediaType mediaType  = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, jsonString);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //在进行回调之前，必须确保listener不为null，以避免空指针异常
                if (listener != null) {
                    //调用其onUploadFailure方法，并将之前捕获的IOException对象e作为参数传递
                    listener.onUploadFailure(e);
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //Log.d(TAG, "onSuccessful: Thread = " + Thread.currentThread().getName());
                if (response.isSuccessful()) {

                    try {
                        Log.d(TAG, "ImageResponseOK");
                        assert response.body() != null;
                        ResponseBody responseBody = response.body();
                        String responseString=responseBody.string();

                        Log.d(TAG, responseString);
                        JSONObject jsonObject= new JSONObject(responseString);
                        System.out.println(jsonObject);
                        String iamge_data = jsonObject.getString("message");
                        Log.d(TAG, "onResponse: "+iamge_data);
                        bitmap=getBitmapFromJsonString(iamge_data);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    //JSONObject jsonObject= new JSONObject(response.body().string());
                    //System.out.println(jsonObject+"111");
                    //ResponseBody responseBody = response.body();
                    /*
                    Log.d(TAG, responseBody.string());
                    String jsonString = responseBody.string()+"111";
                    Log.d(TAG, jsonString);
                     */

                    //在进行回调之前，必须确保listener不为null，以避免空指针异常
                    if (listener != null) {
                        listener.onUploadSuccess(bitmap);
                    }
                } else {
                    //在进行回调之前，必须确保listener不为null，以避免空指针异常
                    if (listener != null) {
                        listener.onUploadFailure(new IOException("Request failed with code: " + response.code()));
                    }
                }
            }
        });
    }

    /**
     * 定义上传结果的监听器接口
     */
    public interface ImageProcessorListener {
        void onUploadSuccess(Bitmap bitmap);
        void onUploadFailure(Exception e);
    }
    public void uploadTextAsync(String url, String jsonString, final TextProcessorListener listener) {
        //1MediaType mediaType = MediaType.parse("application/json"); // 注意这里我们没有指定字符集
        MediaType mediaType  = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, jsonString);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "onFailure: Thread = " + Thread.currentThread().getName());
                //在进行回调之前，必须确保listener不为null，以避免空指针异常
                if (listener != null) {
                    //调用其onUploadFailure方法，并将之前捕获的IOException对象e作为参数传递
                    listener.onUploadFailure(e);
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //Log.d(TAG, "onSuccessful: Thread = " + Thread.currentThread().getName());
                if (response.isSuccessful()) {
                    //
                    Log.d(TAG, "TextResponseOK");
                    assert response.body() != null;
                    ResponseBody responseBody = response.body();
                    String responseString=responseBody.string();
                    //
                    //final String jsonString = response.body().string();
                    //在进行回调之前，必须确保listener不为null，以避免空指针异常
                    if (listener != null) {
                        listener.onUploadSuccess(responseString);
                    }
                } else {
                    //在进行回调之前，必须确保listener不为null，以避免空指针异常
                    if (listener != null) {
                        listener.onUploadFailure(new IOException("Request failed with code: " + response.code()));
                    }
                }
            }
        });
    }
    public interface TextProcessorListener {
        void onUploadSuccess(String json);
        void onUploadFailure(Exception e);
    }


    //输入URI，返回Base64编码的字符串
    public String encodeImageUriToBase64(Context context, Uri imageUri) {
        Bitmap bitmap = null;
        try {
            // 使用ContentResolver从URI加载Bitmap
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);

            // 将Bitmap压缩为JPEG格式（你也可以选择PNG）
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream); // 100是质量参数，范围0-100
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            // 对字节数组进行Base64编码
            String encodedString = Base64.getEncoder().encodeToString(byteArray);
            return encodedString;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 确保回收Bitmap资源（可选，但推荐）
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }
        return null; // 如果发生错误，返回null
    }
    //接收Base64编码的字符串，解码后将图像保存到指定路径
    public Bitmap getBitmapFromJsonString(String base64String) {
        Log.d(TAG, base64String);
        // Base64解码
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);
        Log.d(TAG, "decodeSuss");
        // 创建Bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        // 返回Bitmap
        return bitmap;
    }

}
