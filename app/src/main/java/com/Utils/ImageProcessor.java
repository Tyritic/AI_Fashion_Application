package com.Utils;
import androidx.annotation.NonNull;
import okhttp3.*;
import java.io.IOException;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import okhttp3.MediaType;
public class ImageProcessor {

    private final OkHttpClient client;

    public ImageProcessor() {
        client = new OkHttpClient();
    }

    //定义异步上传图片方法，传参：目标Url，图片字节数组，监听器（用于在异步图像上传任务的不同阶段提供通知或回调）
    public void uploadImageAsync(String url, String jsonString, final ImageProcessorListener listener) {
        //1MediaType mediaType = MediaType.get("application/json"); // 注意这里我们没有指定字符集
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
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    byte[] processedImageBytes = response.body().bytes();
                    //在进行回调之前，必须确保listener不为null，以避免空指针异常
                    if (listener != null) {
                        listener.onUploadSuccess(processedImageBytes);
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
        void onUploadSuccess(byte[] processedImageBytes);
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


}
