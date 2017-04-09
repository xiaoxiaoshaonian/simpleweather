package com.simpleweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/3/28.
 */

public class Suggestion {
    public Comfort comf;
    public CarWash cw;
    public Sport sport;
    public class Comfort{
        @SerializedName("txt")
        public String info;
        public String brf;//简介
    }
    public class CarWash{
        @SerializedName("txt")
        public String info;
        public String brf;//简介

    }
    public class Sport{
        @SerializedName("txt")
        public String info;
        public String brf;//简介

    }


}
