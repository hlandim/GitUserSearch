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
        String url = jsonObject.getString("url");
        String avatar_url = jsonObject.getString("avatar_url");
        String login = jsonObject.getString("login");
        User user = new User(id, url, avatar_url, login);
        return user;

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
}

