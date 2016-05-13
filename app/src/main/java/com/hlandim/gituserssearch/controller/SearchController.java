package com.hlandim.gituserssearch.controller;

import android.content.Context;
import android.content.ContextWrapper;

import com.hlandim.gituserssearch.R;
import com.hlandim.gituserssearch.dao.UserDao;
import com.hlandim.gituserssearch.model.User;
import com.hlandim.gituserssearch.web.GitHubApi;

import java.util.List;

/**
 * Created by hlandim on 5/7/16.
 */
public class SearchController extends ContextWrapper {

    private UserDao mUserDao;

    public SearchController(Context base) {
        super(base);
        mUserDao = new UserDao(base);
    }

    public List<User> getLocalUsers() {
        return mUserDao.list();
    }

    public void removeUser(User user) {
        mUserDao.remove(user);
    }

    public void getGitHubUsers(String search, final GetUsersCallback callback) {
        final GitHubApi gitHubApi = GitHubApi.getInstance();
        gitHubApi.getUsers(search, new GitHubApi.GitHubUsersCallBack() {
            @Override
            public void onGetResponse(List<User> users) {
                if (users != null && users.size() > 0) {
                    final User user = users.get(0);
                    gitHubApi.getHash(user.getUrl(), new GitHubApi.GetHashUrlCallback() {
                        @Override
                        public void onGotHash(String hash) {
                            user.setUrlHash(hash);
                            mUserDao.insert(user);
                            callback.onGotUsers(user);
                        }
                    });

                } else {
                    callback.onGotError(getString(R.string.no_users_found));
                }
            }

            @Override
            public void onGetError(String erroMsg) {
                callback.onGotError(erroMsg);
            }
        });
    }

    public interface GetUsersCallback {
        void onGotUsers(User user);

        void onGotError(String errorMessage);
    }
}