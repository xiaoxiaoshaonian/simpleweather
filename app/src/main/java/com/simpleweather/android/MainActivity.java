package com.simpleweather.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
//        if (sharedPreferences.getString("weather",null)!=null){
//            Intent intent=new Intent(this,WeatherActivity.class);
//            startActivity(intent);
//            finish();
//        }
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_title);
//        toolbar.setTitle("");
//
//        setSupportActionBar(toolbar);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case android.R.id.home:
//               // finish();
//                Log.i("sss","sss");
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

}
