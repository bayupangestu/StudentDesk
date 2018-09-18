package com.project.zhimer.studentdesk;

import android.content.Context;
import android.content.SharedPreferences;


public class SessionManager {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context _context;

    private static final String PREF_NAME = "studentdesk";

    public SessionManager(Context context)
    {
        this._context = context;
        preferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setMahasiswa(String data)
    {
        editor.putString("mahasiswa", data);
        editor.apply();
    }

    public String getMahasiswa()
    {
        return preferences.getString("mahasiswa", "");
    }

    public void setLogin(Boolean data){
        editor.putBoolean("login", data);
        editor.apply();
    }

    public Boolean isLogin(){return preferences.getBoolean("login", false);}


}
