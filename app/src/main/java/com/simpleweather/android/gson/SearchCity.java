package com.simpleweather.android.gson;

import java.util.List;

/**
 * Created by Administrator on 2017/4/7.
 */

public class SearchCity {
    /**
     * basic : {"city":"广安","cnty":"中国","id":"CN101270801","lat":"30.456398","lon":"106.633369","prov":"四川"}
     * status : ok
     */

    private List<HeWeather5Entity> HeWeather5;

    public void setHeWeather5(List<HeWeather5Entity> HeWeather5) {
        this.HeWeather5 = HeWeather5;
    }

    public List<HeWeather5Entity> getHeWeather5() {
        return HeWeather5;
    }

    public static class HeWeather5Entity {
        /**
         * city : 广安
         * cnty : 中国
         * id : CN101270801
         * lat : 30.456398
         * lon : 106.633369
         * prov : 四川
         */

        private BasicEntity basic;
        private String status;

        public void setBasic(BasicEntity basic) {
            this.basic = basic;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public BasicEntity getBasic() {
            return basic;
        }

        public String getStatus() {
            return status;
        }

        public static class BasicEntity {
            private String city;
            private String cnty;
            private String id;
            private String lat;
            private String lon;
            private String prov;

            public void setCity(String city) {
                this.city = city;
            }

            public void setCnty(String cnty) {
                this.cnty = cnty;
            }

            public void setId(String id) {
                this.id = id;
            }

            public void setLat(String lat) {
                this.lat = lat;
            }

            public void setLon(String lon) {
                this.lon = lon;
            }

            public void setProv(String prov) {
                this.prov = prov;
            }

            public String getCity() {
                return city;
            }

            public String getCnty() {
                return cnty;
            }

            public String getId() {
                return id;
            }

            public String getLat() {
                return lat;
            }

            public String getLon() {
                return lon;
            }

            public String getProv() {
                return prov;
            }
        }
    }
}
