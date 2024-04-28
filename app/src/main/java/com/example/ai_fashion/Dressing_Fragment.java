package com.example.ai_fashion;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


public class Dressing_Fragment extends Fragment {
    String user_account;
    String user_password;

    private String latitude;
    private String longitude;
    private String location_response;
    private String weather_response;
    private String address;
    private String province;
    private String city;
    private String district;
    private String township;
    private String adcode;
    private final String api_key="b37606d49c5d3648e1ece38257fd057a";
    private final String location_url_head="https://restapi.amap.com/v3/geocode/regeo?output=json&location=";
    private final String weather_url_head="https://restapi.amap.com/v3/weather/weatherInfo?city=";
    private static final int REQUEST_INTERNET_PERMISSION = 5555;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();//接收从Home_Page和Account_Page传过来的Bundle
        if(bundle!=null)//判空
        {
            //Toast.makeText(getActivity(),"Dressing_Fragment成功接收数据",Toast.LENGTH_SHORT).show();
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
                    // 在这里，你可以获取到 response 的值
                    if(location_response==null)
                    {
                        Toast.makeText(getActivity(), "location_response是空", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        try {
                            JSONObject location_json = new JSONObject(location_response);
                            JSONObject regeocode = location_json.getJSONObject("regeocode");
                            JSONObject addressComponent = regeocode.getJSONObject("addressComponent");
                            province = addressComponent.getString("province");
                            city = addressComponent.getString("city");
                            if(city.equals("[]"))city="";
                            district = addressComponent.getString("district");
                            township = addressComponent.getString("township");
                            adcode = addressComponent.getString("adcode");
                            address = province + city + district+township;
                            Toast.makeText(getActivity(),address, Toast.LENGTH_SHORT).show();
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
                                    //System.out.println(weather_response);
                                    try {
                                        JSONObject weather_json = new JSONObject(weather_response);
                                        JSONObject lives = weather_json.getJSONArray("lives").getJSONObject(0);
                                        String weather = lives.getString("weather");
                                        String temperature = lives.getString("temperature");
                                        Looper.prepare();
                                        Toast.makeText(getActivity(), "天气："+weather+" 温度："+temperature+"°C", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
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