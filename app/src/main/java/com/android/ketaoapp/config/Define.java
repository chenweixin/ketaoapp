package com.android.ketaoapp.config;

import android.graphics.Bitmap;

import com.android.ketaoapp.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * Created by Administrator on 2016/3/22 0022.
 */
public class Define {

//    public static String SERVER_HOST = "http://10.0.2.2:8080/ketao";
    public static String HOST = "http://192.168.1.15";
//public static String HOST = "http://192.168.10.235";
//public static String HOST = "http://125.216.246.25";
    public static String SERVER_HOST = HOST+":8080/ketao";
    public static String IMAGE_HOST = HOST+":8088";
    public static DisplayImageOptions options;

    public static DisplayImageOptions getImageOptions(){
        if(options != null){
            return options;
        }
        else{
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(null)
                    .showImageOnFail(null)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            return options;
        }
    }

    public static String COURSE_TYPES[]={"人文科学","人文科学核心","社会科学","社会科学核心","科学技术","科学技术核心"};

    public static String USER_TYPE = null;
    public static String USER_STUDENT = "KETAO_APP_USER_STUDENT";
    public static String USER_TEACHER = "KETAO_APP_USER_TEACHER";

    public static String USER_NAME = null;
}
