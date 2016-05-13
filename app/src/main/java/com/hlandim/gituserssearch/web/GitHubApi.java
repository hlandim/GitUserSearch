package com.hlandim.gituserssearch.web;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;

import com.hlandim.gituserssearch.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hlandim on 5/7/16.
 */
public class GitHubApi {

    private static final String SEARCH_URL = "https://api.github.com/search/users?q=";
    private static final String TAG = GitHubApi.class.getSimpleName();
    private static final int CODE_ERROR = 1;
    private static final int CODE_SUCCESS = 0;
    private static final String KEY_MSG_SUCCESS = "msgSuccess";
    private static final String KEY_MSG_ERROR = "msgError";
    private static final String KEY_MSG_IMAGE = "image";
    private static GitHubApi instance;

    static {
        System.loadLibrary("git-hub-search-jni");
    }

    private final ExecutorService executorWebService;
    private final ExecutorService executorHashService;

    private GitHubApi() {
        executorWebService = Executors.newFixedThreadPool(1);
        executorHashService = Executors.newFixedThreadPool(1);
    }

    public static GitHubApi getInstance() {
        if (instance == null) {
            instance = new GitHubApi();
        }

        return instance;
    }

    private native long getDjb2HashUrl(String url);

    public void getUsers(final String userName, GitHubUsersCallBack gitHubUsersCallBack) {

        final Handler handler = new CallUserHandler(gitHubUsersCallBack);

        executorWebService.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    String finalUrl = URLEncoder.encode(userName, "utf-8");
                    URL url = new URL(SEARCH_URL + finalUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");

                    if (conn.getResponseCode() != 200) {
                        sendMsg(false, "Failed : HTTP error code : " + conn.getResponseCode());

                    } else {

                        BufferedReader br = new BufferedReader(new InputStreamReader(
                                (conn.getInputStream())));

                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            response.append(line);
                        }
                        List<User> users = getUsersList(response.toString());
                        sendUsersMsg(users);
                    }
                    conn.disconnect();

                } catch (MalformedURLException e) {
                    sendMsg(false, e.getMessage());
                    e.printStackTrace();

                } catch (IOException e) {
                    sendMsg(false, e.getMessage());
                    e.printStackTrace();

                }

            }

            private void sendUsersMsg(List<User> users) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                message.arg1 = CODE_SUCCESS;
                bundle.putParcelableArrayList(KEY_MSG_SUCCESS, new ArrayList<Parcelable>(users));
                message.setData(bundle);
                handler.sendMessage(message);
            }

            private void sendMsg(boolean success, String msg) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                if (success) {
                    message.arg1 = CODE_SUCCESS;
                    bundle.putString(KEY_MSG_SUCCESS, msg);
                } else {
                    message.arg1 = CODE_ERROR;
                    bundle.putString(KEY_MSG_ERROR, msg);

                }
                message.setData(bundle);
                handler.sendMessage(message);
            }

        });

    }

    private Bitmap getImage(URL url) throws Exception {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                return BitmapFactory.decodeStream(connection.getInputStream());
            } else
                return null;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public void getImage(final String urlString, GitHubImageCallBack gitHubImageCallBack) {

        final Handler handler = new CallImageHandler(gitHubImageCallBack);

        executorWebService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    sendImageMsg(getImage(url));
                } catch (Exception e) {
                    sendErrorMsg(e.getMessage());
                }
            }

            private void sendImageMsg(Bitmap bitmap) {
                Message message = new Message();
                Bundle bundle = new Bundle();

                message.arg1 = CODE_SUCCESS;
                bundle.putParcelable(KEY_MSG_IMAGE, bitmap);

                message.setData(bundle);
                handler.sendMessage(message);
            }

            private void sendErrorMsg(String msg) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                message.arg1 = CODE_ERROR;
                bundle.putString(KEY_MSG_ERROR, msg);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });

    }

    public void getHash(final String url, final GetHashUrlCallback getHashUrlCallback) {

        final Handler handler = new CallHashHandler(getHashUrlCallback);

        executorHashService.execute(new Runnable() {
            @Override
            public void run() {
                long hash = getDjb2HashUrl(url);
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putLong("hash", hash);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });

    }

    private List<User> getUsersList(String jsonResponse) {
        List<User> users = new ArrayList<>();
        try {

            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                User user = User.newInstance(jsonObj);
                users.add(user);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        return users;
    }

    public void close() {
        if (executorWebService != null) {
            executorWebService.shutdown();
        }
        if (executorHashService != null) {
            executorHashService.shutdown();
        }
        instance = null;
    }

    public interface GitHubImageCallBack {
        void onGetBitmap(Bitmap bitmap);

        void onGetError(String msgError);
    }

    public interface GitHubUsersCallBack {
        void onGetResponse(List<User> users);

        void onGetError(String erroMsg);
    }


    public interface GetHashUrlCallback {
        void onGotHash(String hash);
    }

    private static class CallImageHandler extends Handler {

        private GitHubImageCallBack gitHubImageCallBack;

        public CallImageHandler(GitHubImageCallBack gitHubImageCallBack) {
            this.gitHubImageCallBack = gitHubImageCallBack;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == CODE_SUCCESS) {
                Bitmap bitmap = msg.getData().getParcelable(KEY_MSG_IMAGE);
                gitHubImageCallBack.onGetBitmap(bitmap);
            } else {
                gitHubImageCallBack.onGetError(msg.getData().getString(KEY_MSG_ERROR));
            }
        }
    }

    private static class CallUserHandler extends Handler {

        private GitHubUsersCallBack gitHubUsersCallBack;

        public CallUserHandler(GitHubUsersCallBack gitHubUsersCallBack) {
            this.gitHubUsersCallBack = gitHubUsersCallBack;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == CODE_SUCCESS) {
                List<User> users = msg.getData().getParcelableArrayList(KEY_MSG_SUCCESS);
                gitHubUsersCallBack.onGetResponse(users);
            } else {
                gitHubUsersCallBack.onGetError(msg.getData().getString(KEY_MSG_ERROR));
            }
        }
    }

    private static class CallHashHandler extends Handler {
        private GetHashUrlCallback getHashUrlCallback;

        public CallHashHandler(GetHashUrlCallback getHashUrlCallback) {
            this.getHashUrlCallback = getHashUrlCallback;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            long hash = 0;
            if (bundle != null) {
                hash = bundle.getLong("hash", 0);
            }
            getHashUrlCallback.onGotHash(String.valueOf(hash));
        }
    }
}
