package com.xujia.loverchat.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xujia.loverchat.db.DbOpenHelper;
import com.xujia.loverchat.utils.Consts;


public class UserDao {
  private DbOpenHelper openHelper;
  private static UserDao instance;

  private UserDao() {
      openHelper = DbOpenHelper.getInstance();
  }
  
  public static UserDao getInstance()   {
      if(instance == null)  {
          instance = new UserDao();
      }
      return instance;
  }
 
  public void saveUser(String userName) {
      SQLiteDatabase db = openHelper.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put(Consts.USERNAME, userName);
      db.insert(Consts.USER_TABLE_NAME, null, values);
     
  }

  public String getUser() {
      String userName = null;
      SQLiteDatabase db = openHelper.getReadableDatabase();
      Cursor cursor = db.query(Consts.USER_TABLE_NAME, new String[]{Consts.USERNAME}, null, null, null, null, null);
      while(cursor.moveToNext())
      {
          userName = cursor.getString(cursor.getColumnIndex(Consts.USERNAME));
      }
      return userName;
  }
  
  public void  deleteUser(String userName) {
      SQLiteDatabase db = openHelper.getWritableDatabase();
      if(db.isOpen()){
          db.delete(Consts.USER_TABLE_NAME, Consts.USERNAME + " = ?", new String[]{userName});
      }
      
  }
}
