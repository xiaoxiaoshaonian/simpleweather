package com.simpleweather.android.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.simpleweather.android.MainActivity;
import com.simpleweather.android.R;
import com.simpleweather.android.WeatherActivity;
import com.simpleweather.android.adapter.AreaAdapter;
import com.simpleweather.android.db.City;
import com.simpleweather.android.db.County;
import com.simpleweather.android.db.Province;
import com.simpleweather.android.util.API;
import com.simpleweather.android.util.HttpUtil;
import com.simpleweather.android.util.SnackbarUtil;
import com.simpleweather.android.util.UtilBaseDataLity;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 获取全国的省 市 区
 * Created by Administrator on 2017/3/28.
 */

public class ChoseAreaFragment extends Fragment {
    private ProgressDialog progressDialog;
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private TextView textView;
    private ListView listView;
    private AreaAdapter arrayAdapter;
    private ArrayList<String> dataList = new ArrayList<>();

    /**
     * 省
     */
    private List<Province> provinceList;
    /**
     * 市
     */
    private List<City> cityList;
    /**
     * 县
     */
    private List<County> countyList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choos_area, container, false);
        textView = (TextView) view.findViewById(R.id.txt_area);
        listView = (ListView) view.findViewById(R.id.listview_area);
        //button = (Button) view.findViewById(R.id.btn_back);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String weatherid = countyList.get(position).getWeatherId();
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherid);
                        intent.putExtra("is_changed_city", 1);
                        startActivity(intent);
                        getActivity().finish();

                    }

            }
        });
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (currentLevel == LEVEL_COUNTY) {
//                    queryCities();
//                } else if (currentLevel == LEVEL_CITY) {
//                    queryProvinces();
//                }
//            }
//        });

        queryProvinces();
        arrayAdapter = new AreaAdapter(dataList);
        listView.setAdapter(arrayAdapter);
    }

    /**
     * 从服务器获取省市的数据
     */
    private void queryFromSever(String address, final String type) {
        showPrograssDialog();
        HttpUtil.sendOkhttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                closePrograssDialog();
                SnackbarUtil.SnackbarUse(getView(), "获取数据失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = UtilBaseDataLity.getProvince(responseText);
                } else if ("city".equals(type)) {
                    result = UtilBaseDataLity.getCity(responseText, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = UtilBaseDataLity.getCounty(responseText, selectedCity.getId());
                }
                //result=true 更新界面
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closePrograssDialog();
                            if ("province".equals(type)) {
                                queryProvinces();

                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 查询全国所有的省 优先查询数据库
     */
    private void queryProvinces() {
        textView.setText("中国");
      //  button.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromSever(API.AREA_URL, "province");
        }
    }

    /**
     * 查询省下面的市 优先从数据库查询
     */
    private void queryCities() {
        textView.setText(selectedProvince.getProvinceName());
       // button.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid=?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int proincecode = selectedProvince.getProvinceCode();
            queryFromSever(API.AREA_URL + "/" + proincecode, "city");
        }

    }

    /**
     * 查询市下面的区 县 优先从数据库查询
     */
    private void queryCounties() {
        textView.setText(selectedCity.getCityName());
      //  button.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid=?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provincecode = selectedProvince.getProvinceCode();
            int cityccode = selectedCity.getCityCode();
            queryFromSever(API.AREA_URL + "/" + provincecode + "/" + cityccode, "county");
        }
    }

    /**
     * 显示进度对话框
     */
    private void showPrograssDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closePrograssDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
