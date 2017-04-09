package com.simpleweather.android.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.simpleweather.android.db.City;
import com.simpleweather.android.db.County;
import com.simpleweather.android.db.Province;
import com.simpleweather.android.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**获取省 市 县区的数据
 * Created by Administrator on 2017/3/28.
 */

public class UtilBaseDataLity {
    /**
     * 获取省级数据
     * @param result
     * @return
     */
    public static boolean getProvince(String result){
        if (!TextUtils.isEmpty(result)){
            try {
                JSONArray jsonArray=new JSONArray(result);
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(jsonObject.getString("name"));
                    province.setProvinceCode(jsonObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }
    /**
     * 获取市级数据
     * @param result
     * @return
     */
    public static boolean getCity(String result,int provinceId){
        if (!TextUtils.isEmpty(result)){
            try {
                JSONArray jsonArray=new JSONArray(result);
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    City city=new City();
                  city.setCityName(jsonObject.getString("name"));
                    city.setCityCode(jsonObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }
    /**
     * 获取县级数据
     * @param result
     * @return
     */
    public static boolean getCounty(String result,int cityId){
        if (!TextUtils.isEmpty(result)){
            try {
                JSONArray jsonArray=new JSONArray(result);
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    County county=new County();
                    county.setCityId(cityId);
                    county.setCountyName(jsonObject.getString("name"));
                    county.setWeatherId(jsonObject.getString("weather_id"));
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    /**
     * 将返回的json数据解析成weather实体类
     * @param result
     * @return
     */
    public static Weather getWeatherData(String result){
        try {
            JSONObject jsonObject=new JSONObject(result);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            String weathercontent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weathercontent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
