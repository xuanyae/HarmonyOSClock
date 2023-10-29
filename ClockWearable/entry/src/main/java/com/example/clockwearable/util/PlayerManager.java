package com.example.clockwearable.util;

import ohos.app.Context;
import ohos.global.resource.BaseFileDescriptor;
import ohos.global.resource.RawFileEntry;
import ohos.media.player.Player;

import java.io.IOException;

public class PlayerManager {
    private Context context;
    private String playerUri;
    private Player player;
    private PlayerStateListener playerStateListener;
    private boolean isPrepared;

    public PlayerManager(Context context, String playerUri) {
        this.context = context;
        this.playerUri = playerUri;
    }

    public void setPlayerStateListener(PlayerStateListener playerStateListener) {
        this.playerStateListener = playerStateListener;
    }

    //实例化
    public void init() {
        player = new Player(context);
        player.setPlayerCallback(new PlayCallBack());
        setResource(playerUri);
    }

    //设置资源路径
    public void setResource(String uri) {
        try {
            RawFileEntry rawFileEntry = context.getResourceManager().getRawFileEntry(uri);
            BaseFileDescriptor baseFileDescriptor = rawFileEntry.openRawFileDescriptor();
            if (!player.setSource(baseFileDescriptor)) {
                return;
            }
            isPrepared = player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //播放音频
    public void play() {
        if (!isPrepared) {
            return;
        }
        if (!player.play()) {
            return;
        }
        player.play();
    }

    //停止播放，释放资源
    public void releasePlayer() {
        if (player == null) {
            return;
        }
        player.stop();
        player.release();
    }

    //判断是否正在播放
    public boolean isPlaying() {
        return player.isNowPlaying();
    }

    private class PlayCallBack implements Player.IPlayerCallback {

        @Override
        public void onPrepared() {

        }

        @Override
        public void onMessage(int i, int i1) {

        }

        @Override
        public void onError(int i, int i1) {

        }

        @Override
        public void onResolutionChanged(int i, int i1) {

        }

        @Override
        public void onPlayBackComplete() {
            playerStateListener.onMusicFinished();
        }

        @Override
        public void onRewindToComplete() {

        }

        @Override
        public void onBufferingChange(int i) {

        }

        @Override
        public void onNewTimedMetaData(Player.MediaTimedMetaData mediaTimedMetaData) {

        }

        @Override
        public void onMediaTimeIncontinuity(Player.MediaTimeInfo mediaTimeInfo) {

        }
    }
}