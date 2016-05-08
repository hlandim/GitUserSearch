package com.hlandim.gituserssearch.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hlandim on 5/7/16.
 */
public class User {

    private String id;
    private String url;
    private String login;
    private String avatar_url;
    private String url_hash;

    private User(String id, String url, String avatar_url, String login) {
        this.id = id;
        this.url = url;
        this.avatar_url = avatar_url;
        this.login = login;
    }

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

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public String getLogin() {
        return login;
    }

    public void setUrl_hash(String url_hash) {
        this.url_hash = url_hash;
    }

    public String getUrl_hash() {
        return url_hash;
    }
}

