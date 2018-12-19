package com.example.onwaier.game2048pic;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

/**
 * Created by Onwaier on 2018/4/8.
 */

public class MyPlayer {

    public final static int INDEX_SONG_MOVE = 0;
    public final static int INDEX_SONG_CANCEL = 1;
    public final static int INDEX_SONG_BEGIN = 2;

    private final static String[] SONG_NAMES = { "move.mp3", "del.wav",
            "num.mp3" };
    // 音效播放
    private static MediaPlayer[] mToneMediaPlayer = new MediaPlayer[SONG_NAMES.length];

    /*
     * 播放声音效果 cancel move
     */
    public static void playTone(Context context, int index) {
        // 加载声音
        AssetManager assetManager = context.getAssets();
        if (mToneMediaPlayer[index] == null) {
            mToneMediaPlayer[index] = new MediaPlayer();
            try {
                AssetFileDescriptor fileDescriptor = assetManager
                        .openFd(SONG_NAMES[index]);
                mToneMediaPlayer[index].setDataSource(
                        fileDescriptor.getFileDescriptor(),
                        fileDescriptor.getStartOffset(),
                        fileDescriptor.getLength());
                mToneMediaPlayer[index].prepare();

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
        mToneMediaPlayer[index].start();
    }
}
