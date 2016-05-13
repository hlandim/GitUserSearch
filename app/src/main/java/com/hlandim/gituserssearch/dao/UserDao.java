package com.hlandim.gituserssearch.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.hlandim.gituserssearch.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hlandim on 5/12/16.
 */
public class UserDao extends ContextWrapper {

    private static final String TABLE_NAME = "user";
    private GitUsersSearchHelper mHelper;
    private SQLiteDatabase mDb;

    public UserDao(Context base) {
        super(base);
        mHelper = new GitUsersSearchHelper(base);
    }

    @NonNull
    public static String getCreateSql() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE ").append(TABLE_NAME).append(" ( ");
        for (Columns columns : Columns.values()) {
            stringBuilder.append(columns.getName()).append(" ").append(columns.getType()).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(" )");

        return stringBuilder.toString();
    }

    public long insert(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Columns.URL.getName(), user.getUrl());
        contentValues.put(Columns.ID_GIT_HUB.getName(), user.getIdGitHub());
        contentValues.put(Columns.LOGIN.getName(), user.getLogin());
        contentValues.put(Columns.AVATAR.getName(), user.getAvatarUrl());
        contentValues.put(Columns.URL_HASH.getName(), user.getUrlHash());

        mDb = mHelper.getWritableDatabase();

        long id = user.getId();
        if (id == 0) {
            id = mDb.insert(TABLE_NAME, null, contentValues);
        } else {
            String where = Columns.ID.getName() + " = " + user.getId();
            mDb.update(TABLE_NAME, contentValues, where, null);
        }

        mDb.close();
        user.setId(id);
        return id;
    }

    public List<User> list() {

        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;
        mDb = mHelper.getReadableDatabase();
        Cursor cursor = mDb.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

//                User user = new User();
                long id = cursor.getLong(cursor.getColumnIndex(Columns.ID.getName()));
                String idGitHub = cursor.getString(cursor.getColumnIndex(Columns.ID_GIT_HUB.getName()));
                String url = cursor.getString(cursor.getColumnIndex(Columns.URL.getName()));
                String login = cursor.getString(cursor.getColumnIndex(Columns.LOGIN.getName()));
                String avatar = cursor.getString(cursor.getColumnIndex(Columns.AVATAR.getName()));
                String url_hash = cursor.getString(cursor.getColumnIndex(Columns.URL_HASH.getName()));

                User user = new User(id, idGitHub, url, avatar, login, url_hash);
                users.add(user);

            } while (cursor.moveToNext());
        }

        mDb.close();

        return users;
    }

    public void remove(User user) {
        String where = Columns.ID.getName() + " = " + user.getId();
        mDb = mHelper.getWritableDatabase();
        mDb.delete(TABLE_NAME, where, null);
        mDb.close();
    }


    private enum Columns {
        ID("_id", "integer primary key autoincrement"),
        ID_GIT_HUB("id_git_hub", "integer"),
        URL("url", "text"),
        LOGIN("login", "text"),
        AVATAR("avatar_url", "text"),
        URL_HASH("url_hash", "text");

        private String mName;
        private String mType;

        Columns(String name, String type) {
            this.mName = name;
            this.mType = type;
        }

        public String getName() {
            return mName;
        }

        public String getType() {
            return mType;
        }
    }
}
