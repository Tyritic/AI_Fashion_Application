package com.example.ai_fashion;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class Dressing_Fragment extends Fragment {

    Button LeisureBtn;
    Button SportBtn;
    Button FormalBtn;
    //Button save_button;
    //Button re_button;
    //ImageView Clothes;
    //ImageView Pants;
    //ImageView Shoes;
    //TextView Clothes_;
    //TextView Pants_;
    //TextView Shoes_;
    String serverUrl;
    String retype;

    String user_account;
    String user_password;
    User user;
    AppDatabase DB;
    RadioGroup radioGroup;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dressing, container, false);
        // Find the button and set the click listener
        TextView temperature_text = view.findViewById(R.id.temperature_text);
        TextView location_text = view.findViewById(R.id.location_text);
        TextView weather_text = view.findViewById(R.id.weather_text);
        ImageView weather_icon = view.findViewById(R.id.weather_icon);
        Button LeisureBtn= view.findViewById(R.id.LeisureBtn);
        Button SportBtn = view.findViewById(R.id.SportBtn);
        Button FormalBtn = view.findViewById(R.id.FormalBtn);
        Button save_button = view.findViewById(R.id.save_button);
        Button re_button = view.findViewById(R.id.re_button);
        ImageView Clothes=view.findViewById(R.id.Clothes);
        ImageView Pants=view.findViewById(R.id.Pants);
        ImageView Shoes=view.findViewById(R.id.Shoes);
        TextView Clothes_=view.findViewById(R.id.Clothe_button);
        TextView Pants_=view.findViewById(R.id.Pant_button);
        TextView Shoes_=view.findViewById(R.id.Shoe_button);

        //检查是否具有网络权限
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，请求网络权限
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.INTERNET}, REQUEST_INTERNET_PERMISSION);
        }

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
                            //解析定位信息
                            JSONObject location_json = new JSONObject(location_response);
                            JSONObject regeocode = location_json.getJSONObject("regeocode");
                            JSONObject addressComponent = regeocode.getJSONObject("addressComponent");
                            city = addressComponent.getString("city");
                            if(city.equals("[]"))city="";
                            district = addressComponent.getString("district");
                            adcode = addressComponent.getString("adcode");
                            location =  city + ","+district;
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
                                        JSONObject weather_json = new JSONObject(weather_response);
                                        JSONObject lives = weather_json.getJSONArray("lives").getJSONObject(0);
                                        String weather = lives.getString("weather");
                                        String temperature = lives.getString("temperature");
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

        /*
        LeisureBtn= view.findViewById(R.id.LeisureBtn);
        SportBtn = view.findViewById(R.id.SportBtn);
        FormalBtn = view.findViewById(R.id.FormalBtn);
        save_button = view.findViewById(R.id.save_button);
        re_button = view.findViewById(R.id.re_button);
        Clothes=view.findViewById(R.id.Clothes);
        Pants=view.findViewById(R.id.Pants);
        Shoes=view.findViewById(R.id.Shoes);
        Clothes_=view.findViewById(R.id.Clothe_button);
        Pants_=view.findViewById(R.id.Pant_button);
        Shoes_=view.findViewById(R.id.Shoe_button);
         */
        DB= Room.databaseBuilder(getActivity(), AppDatabase.class,"Database")
                .allowMainThreadQueries().build();
        user = DB.userDao().findUser(user_account,user_password);
        String user_id = String.valueOf(user.getUser_id());

        //radioGroup = findViewById(R.id.radiogroup);


        //设置性别选择
        /*
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i==R.id.manBtn)
                {
                    user_gender ="man";
                }
                else if(i==R.id.womanBtn)
                {
                    user_gender ="woman";
                }
            }
        });
         */

//

        LeisureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = "Leisure";
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("type", type);
                dataMap.put("user_id", user_id);
                retype=type;
                Gson gson = new Gson();
                String jsonstring = gson.toJson(dataMap);
                ImageProcessor imageProcessor = new ImageProcessor();
                imageProcessor. uploadTextAsync(serverUrl, jsonstring, new ImageProcessor.TextProcessorListener() {
                    @Override
                    public void onUploadSuccess(String json) {
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            String clothes = jsonObject.getString("clothes");
                            String pants = jsonObject.getString("pants");
                            String shoes = jsonObject.getString("shoes");

                            Bitmap bitmap_clothes=imageProcessor.getBitmapFromJsonString(clothes);
                            Bitmap bitmap_pants=imageProcessor.getBitmapFromJsonString(pants);
                            Bitmap bitmap_shoes=imageProcessor.getBitmapFromJsonString(shoes);

                            Clothes.setImageBitmap(bitmap_clothes);
                            Pants.setImageBitmap(bitmap_pants);
                            Shoes.setImageBitmap(bitmap_shoes);

                            Clothes.setVisibility(View.VISIBLE);
                            Pants.setVisibility(View.VISIBLE);
                            Shoes.setVisibility(View.VISIBLE);
                            Clothes_.setVisibility(View.GONE);
                            Pants_.setVisibility(View.GONE);
                            Shoes_.setVisibility(View.GONE);

                            // 打印结果
                            Log.d("JSON_DATA", "clothes: " + clothes + ", pants: " + pants + ", shoes: " + shoes);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onUploadFailure(Exception e) {
                        // 处理上传失败的情况
                        e.printStackTrace();
                        // ...
                    }
                });
            }
    });
        SportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = "Sport";
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("type", type);
                dataMap.put("user_id", user_id);
                retype=type;
                Gson gson = new Gson();
                String jsonstring = gson.toJson(dataMap);
                ImageProcessor imageProcessor = new ImageProcessor();
                imageProcessor. uploadTextAsync(serverUrl, jsonstring, new ImageProcessor.TextProcessorListener() {
                    @Override
                    public void onUploadSuccess(String json) {


                    }

                    @Override
                    public void onUploadFailure(Exception e) {
                        // 处理上传失败的情况
                        e.printStackTrace();
                        // ...
                    }
                });

            }
        });
        FormalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = "Formal";
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("type", type);
                dataMap.put("user_id", user_id);
                retype=type;
                Gson gson = new Gson();
                String jsonstring = gson.toJson(dataMap);
                ImageProcessor imageProcessor = new ImageProcessor();
                imageProcessor. uploadTextAsync(serverUrl, jsonstring, new ImageProcessor.TextProcessorListener() {
                    @Override
                    public void onUploadSuccess(String json) {


                    }

                    @Override
                    public void onUploadFailure(Exception e) {
                        // 处理上传失败的情况
                        e.printStackTrace();
                        // ...
                    }
                });

            }
        });
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        re_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("type", retype);
                dataMap.put("user_id", user_id);
                Gson gson = new Gson();
                String jsonstring = gson.toJson(dataMap);
                ImageProcessor imageProcessor = new ImageProcessor();
                imageProcessor. uploadTextAsync(serverUrl, jsonstring, new ImageProcessor.TextProcessorListener() {
                    @Override
                    public void onUploadSuccess(String json) {


                    }

                    @Override
                    public void onUploadFailure(Exception e) {
                        // 处理上传失败的情况
                        e.printStackTrace();
                        // ...
                    }
                });
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
}