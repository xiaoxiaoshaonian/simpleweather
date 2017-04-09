package com.simpleweather.android;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.simpleweather.android.application.MyApplication;
import com.simpleweather.android.db.Province;
import com.simpleweather.android.gson.Forecast;
import com.simpleweather.android.gson.SearchCity;
import com.simpleweather.android.gson.Weather;
import com.simpleweather.android.util.API;
import com.simpleweather.android.util.HttpUtil;
import com.simpleweather.android.util.SnackbarUtil;
import com.simpleweather.android.util.UtilBaseDataLity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    ImageView imageView;
    CoordinatorLayout coordinator_layout;
    int is_changed_city;//判断是否重新选择了城市
    private String mWeatherid;
    public SwipeRefreshLayout swipe_refresh;
    NestedScrollView nestscollview;
    LinearLayout forecast_linearlayout;
    TextView txt_now_tem, txt_now_info, txt_comfort, txt_car, txt_sport, txt_aqi, txt_pm, txt_now_time;
    CollapsingToolbarLayout collapsingToolbarLayout;

    /**
     * 高德定位
     *
     * @param savedInstanceState
     */
    String loactionCity;//定位到的区
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
//初始化AMapLocationClientOption对象

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    // aMapLocation.getCity();//获取市
                    loactionCity = aMapLocation.getCity();
                    getSearchCityId();
//可在其中解析amapLocation获取相应内容。
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.i("AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        getPermission();
        initView();//初始化控件
        imageView = (ImageView) findViewById(R.id.weather_main_iamge);
        Toolbar toolbar = (Toolbar) findViewById(R.id.weather_toolbar);
        coordinator_layout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        //获取图片;
        getBiYingImag();

        initSharedPreferences();//读取缓存或请求数据
    }

    /**
     * 获取必应一图
     */
    private void getBiYingImag() {
        HttpUtil.sendOkhttpRequest(API.BIYING_IMAG, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SnackbarUtil.SnackbarUse(getWindow().getDecorView(), "获取图片异常...");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String imagurl = response.body().string();
                WeatherActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(imagurl).into(imageView);
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_city:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * 根据天气id获取天气信息
     */
    public void requestWeather(String weatherid) {
        String weatherUrl = API.WEATHER_URL + weatherid + "&key=60d10b897e804994b6f9fda3e3bb5f2e";
        HttpUtil.sendOkhttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SnackbarUtil.SnackbarUse(getWindow().getDecorView(), "获取天气信息异常");
                        swipe_refresh.setRefreshing(false);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                final Weather weather = UtilBaseDataLity.getWeatherData(result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", result);
                            editor.apply();
                            //更新界面
                            showWeatherInfo(weather);
                        } else {
                            coordinator_layout.setVisibility(View.INVISIBLE);
                            SnackbarUtil.SnackbarUse(getWindow().getDecorView(), "获取天气信息失败");
                        }
                        swipe_refresh.setRefreshing(false);
                    }
                });
            }
        });
    }

    /**
     * 处理天气数据和更新界面
     */
    private void showWeatherInfo(Weather weather) {

        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];//更新时间
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        collapsingToolbarLayout.setTitle(cityName + " " + degree);//显示城市名称
        txt_now_info.setText(weatherInfo);//显示当前天气信息
        txt_now_tem.setText(degree);//显示当前温度
        txt_now_time.setText("更新: " + updateTime);//显示更新时间
        forecast_linearlayout.removeAllViews();
        //动态添加未来几天的天气现象
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.forecast_item, forecast_linearlayout, false);
            TextView txt_date = (TextView) view.findViewById(R.id.txt_date);
            TextView txt_info = (TextView) view.findViewById(R.id.txt_info);
            TextView txt_max = (TextView) view.findViewById(R.id.txt_max);
            TextView txt_min = (TextView) view.findViewById(R.id.txt_min);
            txt_date.setText(forecast.date);
            txt_info.setText(forecast.more.info);
            txt_max.setText(forecast.temperature.max);
            txt_min.setText(forecast.temperature.min);
            forecast_linearlayout.addView(view);
        }
        if (weather.aqi != null) {
            txt_aqi.setText(weather.aqi.city.aqi);
            txt_pm.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度: " + weather.suggestion.comf.info;
        String carWash = "洗车指数: " + weather.suggestion.cw.info;
        String sport = "运动建议: " + weather.suggestion.sport.info;
        txt_comfort.setText(comfort);
        txt_sport.setText(sport);
        txt_car.setText(carWash);
         coordinator_layout.setVisibility(View.VISIBLE);
    }

    /**
     * 初始化布局
     */
    private void initView() {
        forecast_linearlayout = (LinearLayout) findViewById(R.id.forecast_linearlayout);
        txt_now_tem = (TextView) findViewById(R.id.txt_now_tem);
        txt_now_info = (TextView) findViewById(R.id.txt_now_info);
        txt_car = (TextView) findViewById(R.id.txt_car);
        txt_comfort = (TextView) findViewById(R.id.txt_comfort);
        txt_sport = (TextView) findViewById(R.id.txt_sport);
        txt_aqi = (TextView) findViewById(R.id.txt_aqi);
        txt_pm = (TextView) findViewById(R.id.txt_pm);
        txt_now_time = (TextView) findViewById(R.id.txt_now_time);
        nestscollview = (NestedScrollView) findViewById(R.id.nestscollview);
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeResources(R.color.colorAccent);
    }

    /**
     * 写入缓存
     */
    private void initSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherstring = sharedPreferences.getString("weather", null);
        is_changed_city = getIntent().getIntExtra("is_changed_city", 0);
        if (weatherstring != null && is_changed_city == 0) {
            //有缓存直接解析
            Weather weather = UtilBaseDataLity.getWeatherData(weatherstring);
            mWeatherid = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            //无缓存去服务器查询天气
            mWeatherid = getIntent().getStringExtra("weather_id");
              coordinator_layout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherid);
        }
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherid);
            }
        });
    }

    /**
     * 高德定位
     */
    public void onLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(MyApplication.getContext());
//设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
        // mLocationOption.setLocationMode(AMapLocationMode.Battery_Saving);
        //获取一次定位结果：
//该方法默认为false。
        //   mLocationOption.setOnceLocation(true);
        mLocationOption.setOnceLocationLatest(true);//获取最近3s内精度最高的一次定位结果：
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否强制刷新WIFI，默认为true，强制刷新。
        mLocationOption.setWifiActiveScan(false);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
//启动定位
        mLocationClient.startLocation();
    }

    //申请权限
    public void getPermission() {
        //这里以ACCESS_COARSE_LOCATION为例
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);//自定义的code
        } else {
            onLocation();//获取定位
        }
    }

    //用户选择权限 允许或拒绝后，会回调onRequestPermissionsResult方法, 该方法类似于onActivityResult方法。
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onLocation();
                } else {
                    SnackbarUtil.SnackbarUse(getWindow().getDecorView(), "请打开权限");
                }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }

    /**
     * 根据城市或经纬度查询 ID;
     */
    public void getSearchCityId() {
        // String address="https://api.heweather.com/v5/search?city="+loactionCity+"&&key=60d10b897e804994b6f9fda3e3bb5f2e";
        HttpUtil.sendOkhttpRequest(API.SEARCH_CITY_ID, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SnackbarUtil.SnackbarUse(getWindow().getDecorView(), "定位城市异常");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                WeatherActivity.this.runOnUiThread(new Runnable() {
                    String string = response.body().string();

                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        SearchCity basicEntity = gson.fromJson(string, SearchCity.class);
                        mWeatherid = basicEntity.getHeWeather5().get(0).getBasic().getId();
                        requestWeather(mWeatherid);


                    }
                });

            }
        });
    }
}
