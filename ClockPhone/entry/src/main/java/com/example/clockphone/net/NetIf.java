package com.example.clockphone.net;

import org.devio.hi.json.HiJson;

import java.util.Map;

public interface NetIf {
    void setClock(Map<String, Integer> params, NetListener listener);

    void getAllClock(NetListener listener);

    void deleteClock(int[] clockid, NetListener listener);

    interface NetListener {
        void onSuccess(HiJson res);

        void onFail(String message);
    }
}
