package com.hlandim.gituserssearch.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hlandim on 5/7/16.
 */
public class User implements Parcelable {

    private String mIdGitHub;
    private long mId;
    private String mUrl;
    private String mLogin;
    private String mAvatarUrl;
    private String mUrlHash;

    public User(long id, String idGitHub, String url, String avatar_url, String login, String urlHash) {
        this.mId = id;
        this.mIdGitHub = idGitHub;
        this.mUrl = url;
        this.mAvatarUrl = avatar_url;
        this.mLogin = login;
        this.mUrlHash = urlHash;
    }

    private User(String idGitHub, String url, String avatar_url, String login) {
        this.mIdGitHub = idGitHub;
        this.mUrl = url;
        this.mAvatarUrl = avatar_url;
        this.mLogin = login;
    }

    protected User(Parcel in) {
        mIdGitHub = in.readString();
        mUrl = in.readString();
        mLogin = in.readString();
        mAvatarUrl = in.readString();
        mUrlHash = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public static User newInstance(JSONObject jsonObject) throws JSONException {
        if (jsonObject == null) {
            return null;
        }
        String id = jsonObject.getString("id");
        String url = jsonObject.getString("html_url");
        String avatar_url = jsonObject.getString("avatar_url");
        String login = jsonObject.getString("login");
        return new User(id, url, avatar_url, login);

    }

    public long getId() {
        return mId;
    }

    public String getIdGitHub() {
        return mIdGitHub;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public String getLogin() {
        return mLogin;
    }

    public String getUrlHash() {
        return mUrlHash;
    }

    public void setUrlHash(String mUrlHash) {
        this.mUrlHash = mUrlHash;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mIdGitHub);
        dest.writeString(this.mAvatarUrl);
        dest.writeString(this.mLogin);
        dest.writeString(this.mUrl);
        dest.writeString(this.mUrlHash);
    }
}

