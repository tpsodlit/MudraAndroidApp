package com.collection.tpwodloffline;


import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "COLLDB.db";
   // private static final int DATABASE_VERSION = 7; //6  -- This version running live on devices before OTS Change and Ezetap
   private static final int DATABASE_VERSION = 8;


    public DatabaseHelper(Context context) {
        //super(context, DATABASE_NAME, context.getExternalFilesDir(null).getAbsolutePath(), null, DATABASE_VERSION);
        //super(context, DATABASE_NAME, context.getFilesDir().getAbsolutePath(),null, DATABASE_VERSION);
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*public void DatabaseHelperR(Context context) {

       //if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
        super(context, DATABASE_NAME, context.getExternalFilesDir(null).getAbsolutePath(), null, DATABASE_VERSION);
       // super(context, DATABASE_NAME, context.getFilesDir().getAbsolutePath(),null, DATABASE_VERSION);
    }*/
}