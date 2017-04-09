package com.simpleweather.android.util;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by Administrator on 2017/3/28.
 */

public class SnackbarUtil {
    public static void SnackbarUse(View view, String title){
        Snackbar.make(view,title,Snackbar.LENGTH_SHORT).setAction("点击",null).show();
    }
}
