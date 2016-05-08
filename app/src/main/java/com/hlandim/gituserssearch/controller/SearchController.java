package com.hlandim.gituserssearch.controller;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;

import com.hlandim.gituserssearch.model.User;
import com.hlandim.gituserssearch.web.GitHubApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hlandim on 5/7/16.
 */
public class SearchController extends ContextWrapper {

    public SearchController(Context base) {
        super(base);
    }

    public void getUsers(String search, final GetUsersCallback callback) {
        GitHubApi.getInstance().getUsers(search, new GitHubApi.GitHubCallBack() {
            @Override
            public void onGetResponse(String jsonResponse) {
                try {
                    List<User> users = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        User user = User.newInstance(jsonObj);
                        users.add(user);
                    }
                    callback.onGotUsers(users);
                } catch (JSONException e) {
                    callback.onGotError(e.getMessage());
                }
            }

            @Override
            public void onGetError(String erroMsg) {
                callback.onGotError(erroMsg);
            }
        });
    }

    public void getUserImage(String url, final GetUserAvatarCallback callback) {
        GitHubApi.getInstance().getImage(url, new GitHubApi.GitHubImageCallBack() {
            @Override
            public void onGetBitmap(Bitmap bitmap) {
                if (callback != null) {
                    callback.onGotAvatar(bitmap);
                }
            }

            @Override
            public void onGetError(String msgError) {
                if (callback != null) {
                    callback.onGotError(msgError);
                }
            }
        });
    }


    public interface GetUsersCallback {
        void onGotUsers(List<User> users);

        void onGotError(String errorMessage);
    }

    public interface GetUserAvatarCallback {
        void onGotAvatar(Bitmap avatar);

        void onGotError(String msgError);
    }
}
