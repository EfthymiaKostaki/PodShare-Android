package com.aueb.podshare.Sessions;

import android.content.Context;
import android.content.SharedPreferences;

public class ImageSharedPreference {
    SharedPreferences sharedPreferences1;
    SharedPreferences.Editor editor1;
    SharedPreferences sharedPreferences2;
    SharedPreferences.Editor editor2;
    String SHARED_PREF_NAME= "image";
    String SESSION_KEY = "image";
    String SHARED_PREF_IMG = "img_extension";
    String IMAGE_KEY = "img_extension";

    public ImageSharedPreference(Context context) {
        sharedPreferences1 = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor1 = sharedPreferences1.edit();
        sharedPreferences2 = context.getSharedPreferences(SHARED_PREF_IMG, Context.MODE_PRIVATE);
        editor2 = sharedPreferences2.edit();
    }

    public void saveSession(String image, String img_extension) {
        editor1.putString(SESSION_KEY, image).commit();
        editor2.putString(IMAGE_KEY, img_extension).commit();
    }

    public String getSession() {
        return sharedPreferences1.getString(SESSION_KEY, null);
    }

    public String getImgExtension(){ return sharedPreferences2.getString(IMAGE_KEY, null); }

    public void terminateSession() {
        editor1.putString(SESSION_KEY, null).commit();
        editor2.putString(IMAGE_KEY, null).commit();
    }
}
