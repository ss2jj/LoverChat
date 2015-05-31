package com.xujia.loverchat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.xujia.loverchat.control.BaseApplication;
import com.xujia.loverchat.utils.Consts;

public class DbOpenHelper extends SQLiteOpenHelper {
    private static final  int DB_VERSION = 1;
    private static DbOpenHelper openHelper;
    private static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
            + Consts.USER_TABLE_NAME + " ("
            + Consts.USERNAME + " TEXT);";
 
    private DbOpenHelper(Context context) {
        super(context, "chat.db", null, DB_VERSION);
    }
    public static DbOpenHelper getInstance() {
        if (openHelper == null) {
            openHelper = new DbOpenHelper(BaseApplication.getGlobalContext());
        }
        return openHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
        // TODO Auto-generated method stub
        arg0.execSQL(USERNAME_TABLE_CREATE); 
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub     
    }
    public void closeDB() {
        if (openHelper != null) {
            try {
                SQLiteDatabase db = openHelper.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            openHelper = null;
        }
    }

}
