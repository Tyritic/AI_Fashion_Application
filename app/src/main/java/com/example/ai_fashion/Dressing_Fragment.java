package com.example.ai_fashion;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.Utils.LocationUtils;

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
    String user_account;
    String user_password;

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