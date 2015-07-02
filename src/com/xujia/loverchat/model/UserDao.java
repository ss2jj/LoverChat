package com.xujia.loverchat.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xujia.loverchat.db.DbOpenHelper;
import com.xujia.loverchat.utils.Consts;

import java.util.HashMap;


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
 
  public void saveUser(String userName,String isValidate) {
      SQLiteDatabase db = openHelper.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put(Consts.USERNAME, userName);
      values.put(Consts.VALIDATE, isValidate);
      db.insert(Consts.USER_TABLE_NAME, null, values);
     
  }

  public HashMap<String,String> getUser() {
      HashMap<String,String>  user = null;
      String userName = null;
      String isValidate = null;
      SQLiteDatabase db = openHelper.getReadableDatabase();
      Cursor cursor = db.query(Consts.USER_TABLE_NAME, new String[]{Consts.USERNAME,Consts.VALIDATE}, null, null, null, null, null);
      while(cursor.moveToNext())
      {
          userName = cursor.getString(cursor.getColumnIndex(Consts.USERNAME));
          isValidate =cursor.getString(cursor.getColumnIndex(Consts.VALIDATE));
          user = new HashMap<String,String>();
          user.put(Consts.USERNAME, userName);
          user.put(Consts.VALIDATE, isValidate);
          
      }
      return user;
  }
  
  public void  deleteUser(String userName) {
      SQLiteDatabase db = openHelper.getWritableDatabase();
      if(db.isOpen()){
          db.delete(Consts.USER_TABLE_NAME, Consts.USERNAME + " = ?", new String[]{userName});
      }
      
  }
  public void  updateUser(String userName,String isValidate) {
      SQLiteDatabase db = openHelper.getWritableDatabase();
      if(db.isOpen()){
          //db.update(Consts.USER_TABLE_NAME, Consts.USERNAME + " = ?", new String[]{userName});
          ContentValues values = new ContentValues();
          values.put(Consts.VALIDATE, isValidate);
          db.update(Consts.USER_TABLE_NAME, values, Consts.USERNAME + " = ?", new String[]{userName});
      }
  }
}
