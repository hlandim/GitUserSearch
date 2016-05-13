package com.hlandim.gituserssearch.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hlandim on 5/12/16.
 */
public class GitUsersSearchHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "gitUsersSearch.db";
    private static final int VERSION = 1;

    public GitUsersSearchHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserDao.getCreateSql());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
