package com.android.ketaoapp.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/3/28 0028.
 */
public class LocalSharePreferences {
    public static boolean commintName(Context context, String key, String username){
        SharedPreferences sharedPreferences = context.getSharedPreferences("ketao", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, username);
        editor.commit();
        return true;
    }

    public static String getName(Context context, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences("ketao", 0);
        String value = sharedPreferences.getString(key, "");
        return value;
    }

    public static void clear(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("ketao", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
