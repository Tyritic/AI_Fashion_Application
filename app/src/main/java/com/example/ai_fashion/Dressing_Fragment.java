package com.example.ai_fashion;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.DB.AppDatabase;
import com.JavaBean.User;
import com.Utils.ImageProcessor;
import com.Utils.LocationUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class Dressing_Fragment extends Fragment {
    boolean b;
    String clothes;
    String pants;
    String shoes;

    private static final String TAG = "MyActivity";
    //String serverUrl="b37606d49c5d3648e1ece38257fd057a";
    String serverUrl="https://47cd-58-82-220-12.ngrok-free.app/Recommend";
    String type;

    String user_account;
    String user_password;
    User user;
    AppDatabase DB;

    //定位组件
    private String latitude;
    private String longitude;
    private String location_response;
    private String weather_response;
    private String location;
    private String city;
    private String district;
    private String adcode;
    private final String api_key="b37606d49c5d3648e1ece38257fd057a";
    private final String location_url_head="https://restapi.amap.com/v3/geocode/regeo?output=json&location=";
    private final String weather_url_head="https://restapi.amap.com/v3/weather/weatherInfo?city=";
    private static final int REQUEST_INTERNET_PERMISSION = 5555;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //定义天气图标和天气的对应关系
        Map<String, List<String>> iconWeatherMap = new HashMap<>();
        iconWeatherMap.put("风", Arrays.asList("有风", "平静", "微风", "和风", "清风", "强风/劲风", "疾风", "大风", "烈风", "风暴", "狂爆风", "飓风", "热带风暴", "龙卷风"));
        iconWeatherMap.put("多云", Arrays.asList("阴","少云", "晴间多云", "多云"));
        iconWeatherMap.put("雪", Arrays.asList("雪", "阵雪", "小雪", "中雪", "大雪", "暴雪", "小雪-中雪", "中雪-大雪", "大雪-暴雪", "冷"));
        iconWeatherMap.put("雾", Arrays.asList("浮尘", "扬沙", "沙尘暴", "强沙尘暴", "雾", "浓雾", "强浓雾", "轻雾", "大雾", "特强浓雾"));
        iconWeatherMap.put("晴", Arrays.asList("晴", "热"));
        iconWeatherMap.put("雨夹雪", Arrays.asList("雨雪天气", "雨夹雪", "阵雨夹雪"));
        iconWeatherMap.put("雨", Arrays.asList("阵雨", "雷阵雨", "雷阵雨并伴有冰雹", "小雨", "中雨", "大雨", "暴雨", "大暴雨", "特大暴雨", "强阵雨", "强雷阵雨", "极端降雨", "毛毛雨/细雨", "雨", "小雨-中雨", "中雨-大雨", "大雨-暴雨", "暴雨-大暴雨", "大暴雨-特大暴雨", "冻雨"));
        iconWeatherMap.put("霾", Arrays.asList("霾", "中度霾", "重度霾", "严重霾", "未知"));

        //接收从Home_Page和Account_Page传过来的Bundle
        Bundle bundle = getArguments();//接收从Home_Page和Account_Page传过来的Bundle
        if(bundle!=null)//判空
        {
            user_account = bundle.getString("user_account");
            user_password = bundle.getString("user_password");
        }
        else
        {
            Toast.makeText(getActivity(),"Dressing_Fragment未接收数据",Toast.LENGTH_SHORT).show();
        }
        // 设置布局文件
        View view = inflater.inflate(R.layout.fragment_dressing, container, false);

        //检查是否具有网络权限
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，请求网络权限
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.INTERNET}, REQUEST_INTERNET_PERMISSION);
        }

        // 绑定组件
        TextView temperature_text = view.findViewById(R.id.temperature_text);
        TextView location_text = view.findViewById(R.id.location_text);
        TextView weather_text = view.findViewById(R.id.weather_text);
        ImageView weather_icon = view.findViewById(R.id.weather_icon);
        RadioGroup radioGroup = view.findViewById(R.id.radiogroup);
        Button save_button = view.findViewById(R.id.save_button);
        Button re_button = view.findViewById(R.id.re_button);
        Button button_my_dressing=view.findViewById(R.id.mine_button);
        ImageView Clothes_image=view.findViewById(R.id.Clothes);
        ImageView Pants_image=view.findViewById(R.id.Pants);
        ImageView Shoes_image=view.findViewById(R.id.Shoes);
        TextView Clothes_text=view.findViewById(R.id.Clothe_button);
        TextView Pants_text=view.findViewById(R.id.Pant_button);
        TextView Shoes_text=view.findViewById(R.id.Shoe_button);
        Button advice_button = view.findViewById(R.id.advice_button);

        //设置Advice按钮
        advice_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //建立一个Intent对象，用于启动Advice_Page
                Intent intent = new Intent(getActivity(), Advice_Page.class);
                if(user_account!=null&&user_password!=null){
                    intent.putExtra("user_account",user_account);
                    intent.putExtra("user_password",user_password);
                }
                else{
                    Toast.makeText(getActivity(), "Dressing_Frament向Advice_Page传输为空", Toast.LENGTH_SHORT).show();
                }
                startActivity(intent);
            }
        });

        //设置场合选择
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i==R.id.LeisureBtn)
                {
                    type ="Leisure";
                }
                else if(i==R.id.SportBtn)
                {
                    type ="Sport";
                }
                else if(i==R.id.FormalBtn)
                {
                    type ="Formal";
                }
            }
        });

        // 获取定位信息
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    String location_response = (String) msg.obj;
                    if(location_response==null)
                    {
                        Toast.makeText(getActivity(), "location_response是空", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        try {
                            //解析传回来的定位信息
                            JSONObject location_json = new JSONObject(location_response);
                            JSONObject regeocode = location_json.getJSONObject("regeocode");
                            JSONObject addressComponent = regeocode.getJSONObject("addressComponent");
                            city = addressComponent.getString("city");
                            if(city.equals("[]"))city="";
                            district = addressComponent.getString("district");
                            adcode = addressComponent.getString("adcode");
                            location =  city + ","+district;

                            // 更新UI
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 更新UI操作
                                    location_text.setText(location);
                                }
                            });

                            // 获取天气信息
                            new Thread(() -> {
                                weather_response = getWeather(adcode);
                                if(weather_response==null)
                                {
                                    Looper.prepare();
                                    Toast.makeText(getActivity(), "weather_response是空", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else
                                {
                                    try {
                                        //解析传回来的天气信息
                                        JSONObject weather_json = new JSONObject(weather_response);
                                        JSONObject lives = weather_json.getJSONArray("lives").getJSONObject(0);
                                        String weather = lives.getString("weather");
                                        String temperature = lives.getString("temperature");

                                        // 更新UI
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                // 更新UI操作
                                                    temperature_text.setText(temperature+"°C");
                                                    weather_text.setText(weather);
                                                if(Objects.requireNonNull(iconWeatherMap.get("风")).contains(weather))
                                                {
                                                    weather_icon.setImageResource(R.drawable.windy);
                                                }
                                                else if(Objects.requireNonNull(iconWeatherMap.get("多云")).contains(weather))
                                                {
                                                    weather_icon.setImageResource(R.drawable.cloudy);
                                                }
                                                else if(Objects.requireNonNull(iconWeatherMap.get("雪")).contains(weather))
                                                {
                                                    weather_icon.setImageResource(R.drawable.snowy);
                                                }
                                                else if(Objects.requireNonNull(iconWeatherMap.get("雾")).contains(weather))
                                                {
                                                    weather_icon.setImageResource(R.drawable.foggy);
                                                }
                                                else if(Objects.requireNonNull(iconWeatherMap.get("晴")).contains(weather))
                                                {
                                                    weather_icon.setImageResource(R.drawable.sunny);
                                                }
                                                else if(Objects.requireNonNull(iconWeatherMap.get("雨夹雪")).contains(weather))
                                                {
                                                    weather_icon.setImageResource(R.drawable.sleet);
                                                }
                                                else if(Objects.requireNonNull(iconWeatherMap.get("雨")).contains(weather))
                                                {
                                                    weather_icon.setImageResource(R.drawable.rainy);
                                                }
                                                else if(Objects.requireNonNull(iconWeatherMap.get("霾")).contains(weather))
                                                {
                                                    weather_icon.setImageResource(R.drawable.smog);
                                                }
                                            }
                                        });
                                    }
                                    catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

        LocationUtils.getInstance(getActivity()).getLocation(new LocationUtils.LocationCallBack() {
            @Override
            public void setLocation(Location location) {
                if (location != null){
                    latitude = String.valueOf(location.getLatitude());
                    longitude = String.valueOf(location.getLongitude());
                }
                else {
                    Toast.makeText(getActivity(), "location是空", Toast.LENGTH_SHORT).show();
                }
                if(latitude==null||longitude==null)
                {
                    Toast.makeText(getActivity(),"未获取到经纬度",Toast.LENGTH_SHORT).show();
                }
                new Thread(() -> {
                    location_response = getAddress(longitude, latitude);
                    Message message = new Message();
                    message.what = 1;
                    message.obj = location_response;
                    handler.sendMessage(message);
                }).start();
            }
        });

        DB= Room.databaseBuilder(getActivity(), AppDatabase.class,"Database")
                .allowMainThreadQueries().build();
        user = DB.userDao().findUser(user_account,user_password);
        String user_id = String.valueOf(user.getUser_id());

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clothes!=null&&pants!=null&&shoes!=null)
                {
                    int num=0;
                    String user_frame_name=""+user.getUser_id();
                    File directory = getActivity().getFilesDir();
                    // 访问多级目录
                    File user_frame = new File(directory, user_frame_name);
                    File dressing = new File(user_frame, "dressing");
                    File files[]=dressing.listFiles();
                    if(files!=null)
                    {
                        num=files.length;
                    }
                    File dressing_i =new File(dressing,"dressing"+num);
                    dressing_i.mkdir();
                    File clothes_f = new File(dressing_i, "clothes.png");
                    File trousers_f = new File(dressing_i, "trousers.png");
                    File shoes_f = new File(dressing_i, "shoes.png");
                    shoes_f.mkdir();
                    trousers_f.mkdir();
                    clothes_f.mkdir();
                    File wardrobe = new File(user_frame, "wardrobe");
                    File clothess = new File(wardrobe, "clothes");
                    File trouserss = new File(wardrobe, "trousers");
                    File shoess = new File(wardrobe, "shoes");
                    File clothes_p = new File(wardrobe, "clothes_"+clothes+".png");
                    File trousers_p = new File(wardrobe, "trousers_"+pants+".png");
                    File shoes_p = new File(wardrobe, "shoes_"+shoes+".png");
                    String clothesImagePath =  clothess.getPath()+"/clothes_"+clothes+".png";
                    String pantsImagePath = trouserss.getPath()+"/trousers_"+pants+".png";
                    String shoesImagePath = shoess.getPath()+"/shoes_"+shoes+".png";
                    try{
                        copyFile(clothes_p, clothes_f);
                    }catch (Exception e)
                    {
                        System.out.println("复制失败");
                    }
                    try{
                        copyFile(trousers_p, trousers_f);
                    }catch (Exception e)
                    {
                        System.out.println("复制失败");
                    }
                    try{
                        copyFile(shoes_p, shoes_f);
                    }catch (Exception e)
                    {
                        System.out.println("复制失败");
                    }

                }

            }
        });

        re_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b=true;
                /*
                Clothes_image.setVisibility(View.GONE);
                Pants_image.setVisibility(View.GONE);
                Shoes_image.setVisibility(View.GONE);
                Clothes_text.setVisibility(View.VISIBLE);
                Pants_text.setVisibility(View.VISIBLE);
                Shoes_text.setVisibility(View.VISIBLE);
                 */
                if(type==null)
                {
                    Toast.makeText(getActivity(),"请选择场合",Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("type", type);
                dataMap.put("user_id", user_id);
                Gson gson = new Gson();
                String jsonstring = gson.toJson(dataMap);
                ImageProcessor imageProcessor = new ImageProcessor();

                /*
                String user_frame_name=""+user.getUser_id();
                File directory = getActivity().getFilesDir();
                // 访问多级目录
                File user_frame = new File(directory, user_frame_name);
                File wardrobe = new File(user_frame, "wardrobe");
                File clothess = new File(wardrobe, "clothes");
                File trouserss = new File(wardrobe, "trousers");
                File shoess = new File(wardrobe, "shoes");
                //等待规定返回文件名，通过文件名提取图片绝对路径l
                //根据绝对路径设置imageView显示
                String clothesImagePath =  clothess.getPath()+"/clothes_"+9+".png";
                String pantsImagePath = trouserss.getPath()+"/trousers_"+0+".png";
                String shoesImagePath = shoess.getPath()+"/shoes_"+0+".png";
                Log.d(TAG,clothesImagePath+"   "+pantsImagePath+"   "+shoesImagePath);
                Bitmap  clothesbitmap = BitmapFactory.decodeFile(clothesImagePath);
                Bitmap pantsbitmap = BitmapFactory.decodeFile(pantsImagePath);
                Bitmap shoesitmap = BitmapFactory.decodeFile(shoesImagePath);
                Log.d(TAG,"bitmapOK");
                if ((clothesbitmap != null)&&(pantsbitmap != null)&& (shoesitmap != null)){
                    // 设置位图到ImageView
                    Clothes_image.setImageBitmap( clothesbitmap);
                    Pants_image.setImageBitmap(pantsbitmap);
                    Shoes_image.setImageBitmap(shoesitmap);
                } else {
                    // 位图加载失败，你可以在这里处理错误情况，比如显示一个默认图片或错误信息
                    Log.e("Image Loading", "Failed to load bitmap from " + clothesImagePath+"   "+ pantsImagePath+"   "+shoesImagePath);
                }

//                                    //控制组件的显示和隐藏
                Clothes_image.setVisibility(View.VISIBLE);
                Pants_image.setVisibility(View.VISIBLE);
                Shoes_image.setVisibility(View.VISIBLE);
                Clothes_text.setVisibility(View.GONE);
                Pants_text.setVisibility(View.GONE);
                Shoes_text.setVisibility(View.GONE);
                 */
                // 打印结果
                imageProcessor. uploadTextAsync(serverUrl, jsonstring, new ImageProcessor.TextProcessorListener() {
                            @Override
                            public void onUploadSuccess(String json) {
                                try {

                                    JSONObject jsonObject = new JSONObject(json);
                                    clothes = jsonObject.getString("clothes");
                                    pants = jsonObject.getString("pants");
                                    shoes = jsonObject.getString("shoes");
                                    Log.d("JSON_DATA", "clothes: " + clothes + ", pants: " + pants + ", shoes: " + shoes);
                                    b=false;
                                    /*
                                    String user_frame_name=""+user.getUser_id();
                                    File directory = getActivity().getFilesDir();
                                    // 访问多级目录
                                    File user_frame = new File(directory, user_frame_name);
                                    File wardrobe = new File(user_frame, "wardrobe");
                                    File clothess = new File(wardrobe, "clothes");
                                    File trouserss = new File(wardrobe, "trousers");
                                    File shoess = new File(wardrobe, "shoes");
                                    //等待规定返回文件名，通过文件名提取图片绝对路径
                                    //根据绝对路径设置imageView显示


                                     String clothesImagePath =  clothess.getPath()+"/clothes_"+"9"+".png";
                                    String pantsImagePath = trouserss.getPath()+"/trousers_"+pants+".png";
                                    String shoesImagePath = shoess.getPath()+"/shoes_"+shoes+".png";
                                    Log.d("JSON_DATA", "clothes_: " + clothesImagePath + ", pants_: " + pantsImagePath + ", shoes_: " + shoesImagePath);
                                    Log.d(TAG,"pathOK");
                                    Bitmap  clothesbitmap = BitmapFactory.decodeFile(clothesImagePath);
                                    Bitmap pantsbitmap = BitmapFactory.decodeFile(pantsImagePath);
                                    Bitmap shoesitmap = BitmapFactory.decodeFile(shoesImagePath);
                                    Log.d(TAG,"bitmapOK");
                                    if ((clothesbitmap != null)&&(pantsbitmap != null)&& (shoesitmap != null)){
                                        // 设置位图到ImageView
                                        Clothes_image.setImageBitmap( clothesbitmap);
                                        Pants_image.setImageBitmap(pantsbitmap);
                                        Shoes_image.setImageBitmap(shoesitmap);
                                    } else {
                                        // 位图加载失败，你可以在这里处理错误情况，比如显示一个默认图片或错误信息
                                        Log.e("Image Loading", "Failed to load bitmap from " + clothesImagePath+"   "+ pantsImagePath+"   "+shoesImagePath);
                                    }

//                                    //控制组件的显示和隐藏
                                    Clothes_image.setVisibility(View.VISIBLE);
                                    Pants_image.setVisibility(View.VISIBLE);
                                    Shoes_image.setVisibility(View.VISIBLE);
                                    Clothes_text.setVisibility(View.GONE);
                                    Pants_text.setVisibility(View.GONE);
                                    Shoes_text.setVisibility(View.GONE);
                                     */

                                    // 打印结果



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onUploadFailure(Exception e) {
                                // 处理上传失败的情况
                                e.printStackTrace();
                            }


                });
                //
                while(b);
                String user_frame_name=""+user.getUser_id();
                File directory = getActivity().getFilesDir();
                // 访问多级目录
                File user_frame = new File(directory, user_frame_name);
                File wardrobe = new File(user_frame, "wardrobe");
                File clothess = new File(wardrobe, "clothes");
                File trouserss = new File(wardrobe, "trousers");
                File shoess = new File(wardrobe, "shoes");
                String clothesImagePath =  clothess.getPath()+"/clothes_"+"9"+".png";
                String pantsImagePath = trouserss.getPath()+"/trousers_"+pants+".png";
                String shoesImagePath = shoess.getPath()+"/shoes_"+shoes+".png";
                Log.d("JSON_DATA", "clothes_: " + clothesImagePath + ", pants_: " + pantsImagePath + ", shoes_: " + shoesImagePath);
                Log.d(TAG,"pathOK");
                Bitmap  clothesbitmap = BitmapFactory.decodeFile(clothesImagePath);
                Bitmap pantsbitmap = BitmapFactory.decodeFile(pantsImagePath);
                Bitmap shoesitmap = BitmapFactory.decodeFile(shoesImagePath);
                Log.d(TAG,"bitmapOK");
                if ((clothesbitmap != null)&&(pantsbitmap != null)&& (shoesitmap != null)){
                    // 设置位图到ImageView
                    Clothes_image.setImageBitmap( clothesbitmap);
                    Pants_image.setImageBitmap(pantsbitmap);
                    Shoes_image.setImageBitmap(shoesitmap);
                } else {
                    // 位图加载失败，你可以在这里处理错误情况，比如显示一个默认图片或错误信息
                    Log.e("Image Loading", "Failed to load bitmap from " + clothesImagePath+"   "+ pantsImagePath+"   "+shoesImagePath);
                }

//                                    //控制组件的显示和隐藏
                Clothes_image.setVisibility(View.VISIBLE);
                Pants_image.setVisibility(View.VISIBLE);
                Shoes_image.setVisibility(View.VISIBLE);
                Clothes_text.setVisibility(View.GONE);
                Pants_text.setVisibility(View.GONE);
                Shoes_text.setVisibility(View.GONE);
            }
        });
       /*
        mine_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), My_Dressing.class);
                if(user_account!=null&&user_password!=null){
                    intent.putExtra("user_account",user_account);
                    intent.putExtra("user_password",user_password);
                }
                else{
                    Toast.makeText(getActivity(), "Dressing_Frament向My_Dressing传输为空", Toast.LENGTH_SHORT).show();
                }
                startActivity(intent);
            }
    });
        */

        button_my_dressing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //建立一个Intent对象，用于启动my_dressing
                Intent intent = new Intent(getActivity(), My_Dressing.class);
                if(user_account!=null&&user_password!=null)
                {
                    //Toast.makeText(getActivity(),"Dressing_Fragment向My_Dressing发送成功",Toast.LENGTH_SHORT).show();
                    intent.putExtra("user_account", user_account);
                    intent.putExtra("user_password", user_password);
                }
                else
                {
                    Toast.makeText(getActivity(),"Dressing_Fragment向My_Dressing发送失败",Toast.LENGTH_SHORT).show();
                }
                startActivity(intent);
            }
        });
        return view;
    }

    //获取地址信息
    public String getAddress(String lon, String lat) {
        String urlString = location_url_head + lon + "," + lat + "&key="+api_key+"&radius=1000&extensions=base";
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
                return response.toString();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取天气信息
    public String getWeather (String adcode) {
        String urlString = weather_url_head + adcode + "&key="+api_key+"&output=json&extensions=base";
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
                return response.toString();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void copyFile(File source, File dest)
            throws IOException {
        Files.copy(source.toPath(), dest.toPath());
    }
}