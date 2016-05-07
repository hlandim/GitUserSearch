package com.hlandim.gituserssearch.web;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hlandim on 5/7/16.
 */
public class GitHubApi {

    private static final String SEARCH_URL = "https://api.github.com/search/users?q=";
    private static final int CODE_ERROR = 1;
    private static final int CODE_SUCCESS = 0;
    private static final String KEY_MSG_SUCCESS = "msgSuccess";
    private static final String KEY_MSG_ERROR = "msgError";
    private static final String KEY_MSG_IMAGE = "image";
    private static GitHubApi instance;
    private ExecutorService executorService;

    private GitHubApi() {
        executorService = Executors.newFixedThreadPool(1);
    }

    public static GitHubApi getInstance() {
        if (instance == null) {
            instance = new GitHubApi();
        }

        return instance;
    }


    public void getUsers(final String userName, final GitHubCallBack gitHubCallBack) {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.arg1 == CODE_SUCCESS) {
                    gitHubCallBack.onGetResponse(msg.getData().getString(KEY_MSG_SUCCESS));
                } else {
                    gitHubCallBack.onGetError(msg.getData().getString(KEY_MSG_ERROR));
                }
            }
        };

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {


                    URL url = new URL(SEARCH_URL + userName);
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
                        sendMsg(true, response.toString());
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

    public void getImage(final String urlString, final GitHubImageCallBack gitHubImageCallBack) {

        final Handler handler = new Handler() {
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
        };

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    sendImageMsg(getImage(url));
                } catch (Exception e) {
                    sendMsg(false, e.getMessage());
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

    public void close() {
        if (executorService != null) {
            executorService.shutdown();
        }
        instance = null;
    }

    public interface GitHubImageCallBack {
        void onGetBitmap(Bitmap bitmap);

        void onGetError(String msgError);
    }

    public interface GitHubCallBack {
        void onGetResponse(String jsonResponse);

        void onGetError(String erroMsg);
    }
}
