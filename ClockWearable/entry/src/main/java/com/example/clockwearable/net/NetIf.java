package com.example.clockwearable.net;

import org.devio.hi.json.HiJson;

import java.util.Map;

public interface NetIf {
    void getAllClock(NetListener listener);

    void setHappened(Map<String, Integer> params, NetListener listener);

    void setSends(Map<String, Integer> params, NetListener listener);

    interface NetListener {
        void onSuccess(HiJson res);

        void onFail(String message);
    }
}
