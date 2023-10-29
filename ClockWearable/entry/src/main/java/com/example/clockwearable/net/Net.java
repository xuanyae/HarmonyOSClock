package com.example.clockwearable.net;

import ohos.net.NetHandle;
import ohos.net.NetManager;
import org.devio.hi.json.HiJson;
import org.devio.hi.json.JSONArray;
import org.devio.hi.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class Net implements NetIf {
    private NetManager netManager;
    private static final String URL = "http://abcdd.vaiwan.com";

    public Net() {
        netManager = NetManager.getInstance(null);
    }

    //拼接URL
    public static String buildParams(String url, Map<String, Integer> params) {
        String URLS = URL + url;
        if (params == null) return null;
        StringBuilder builder = new StringBuilder(URLS);
        boolean isFirst = true;
        for (String key : params.keySet()) {
            Integer value = params.get(key);
            if (key != null && value != null) {
                if (isFirst) {//第一次拼接时需拼接?号
                    isFirst = false;
                    builder.append("?");
                } else {
                    builder.append("&");
                }
                builder.append(key).append("=").append(value);
            }
        }
        return builder.toString();
    }

    //向服务器发送GET请求
    private void doGet(String finalUrl, NetListener listener) {
        NetHandle netHandle = netManager.getDefaultNet();
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            URL url = new URL(finalUrl);
            URLConnection urlConnection = netHandle.openConnection(url, Proxy.NO_PROXY);
            if (urlConnection instanceof HttpURLConnection) {
                connection = (HttpURLConnection) urlConnection;
            }
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                byteArrayOutputStream = new ByteArrayOutputStream();
                int readLen;
                byte[] bytes = new byte[1024];
                while ((readLen = inputStream.read(bytes)) != -1) {
                    byteArrayOutputStream.write(bytes, 0, readLen);
                }
                String result = byteArrayOutputStream.toString();
                ThreadExecutor.runUI(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!"".equals(result)) {
                                HiJson hiJson = new HiJson(new JSONArray(result));
                                listener.onSuccess(hiJson);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onFail("数据解析错误 code:" + e.toString());
                        }
                    }
                });
            } else {
                listener.onFail("请求失败 code:" + connection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFail("请求失败 msg:" + e.toString());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void getAllClock(NetListener listener) {//获取所有闹钟
        String finalUrl = URL + "/searchAllClock";
        ThreadExecutor.runBG(new Runnable() {
            @Override
            public void run() {
                doGet(finalUrl, listener);
            }
        });
    }

    @Override
    public void setHappened(Map<String, Integer> params, NetListener listener) {
        String finalUrl = buildParams("/setHappened", params);
        ThreadExecutor.runBG(new Runnable() {
            @Override
            public void run() {
                doGet(finalUrl, listener);
            }
        });
    }

    @Override
    public void setSends(Map<String, Integer> params, NetListener listener) {
        String finalUrl = buildParams("/setSends", params);
        ThreadExecutor.runBG(new Runnable() {
            @Override
            public void run() {
                doGet(finalUrl, listener);
            }
        });
    }
}
