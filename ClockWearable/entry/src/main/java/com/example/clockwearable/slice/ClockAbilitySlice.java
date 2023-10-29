package com.example.clockwearable.slice;

import com.example.clockwearable.ResourceTable;
import com.example.clockwearable.net.Net;
import com.example.clockwearable.net.NetIf;
import com.example.clockwearable.util.PlayerManager;
import com.example.clockwearable.util.PlayerStateListener;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import org.devio.hi.json.HiJson;

import java.util.HashMap;
import java.util.Map;

public class ClockAbilitySlice extends AbilitySlice {
    private PlayerManager playerManager;
    private Net net = new Net();
    private int clockid;

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_clock);
        clockid = intent.getIntParam("clockid", 0);
        initPlayer();
        Button btnClose = (Button) findComponentById(ResourceTable.Id_btn_close);
        btnClose.setClickedListener((button) -> {
            if (playerManager.isPlaying()) {
                playerManager.releasePlayer();
            }
            setHappened();
            this.terminate();//暂时直接关闭
        });
    }

    //当闹钟响完时设置该闹钟的happened值为1
    private void setHappened() {
        Map<String, Integer> params = new HashMap<>();
        params.put("clockid", clockid);
        try {
            net.setHappened(params, new NetIf.NetListener() {

                @Override
                public void onSuccess(HiJson res) {
                }

                @Override
                public void onFail(String message) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPlayer() {
        playerManager = new PlayerManager(this, "resources/rawfile/Alarm.mp3");
        playerManager.setPlayerStateListener(new PlayerStateListener() {
            @Override
            public void onMusicFinished() {
                setHappened();
                terminate();
            }
        });
        playerManager.init();
        playerManager.play();
    }
}
