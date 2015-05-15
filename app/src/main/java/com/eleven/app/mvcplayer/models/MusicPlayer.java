package com.eleven.app.mvcplayer.models;

import android.media.MediaPlayer;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by eleven on 15/5/15.
 * 音乐播放器
 */
public class MusicPlayer {
    public interface PlayerListener {
        void onPlay();
        void onPause();
        void onResume();
        void onPlayNext();
        void onPlayPrev();
        void onProgressUpdate(int progress);
    }

    private static MusicPlayer sMusicPlayer;
    private List<Music> mPlayList;          // 播放列表
    private boolean mPlaying;               // 是否正在播放
    private int mCurrentIndex;              // 目前播放的位置
    private int mCurrentProgress;           // 播放进度
    private MediaPlayer mMediaPlayer;       // 播放器
    private PlayerListener mListener;       // 监听器
    private Timer mTimer;                   // 计时器
    private int mTotalTime;                 // 播放时间


    public static MusicPlayer getInstance() {
        if (sMusicPlayer == null) {
            sMusicPlayer = new MusicPlayer();
        }
        return sMusicPlayer;
    }

    public void play(final int index) {
        if (index < 0 || index >= mPlayList.size()) {
            return;
        }
        // 如果播放的歌曲是目前的歌曲
        Music music = mPlayList.get(index);
        if (music.equals(mPlayList.get(mCurrentIndex))) {
            // 恢复播放
            if (!mPlaying) {
                mMediaPlayer.start();
                return;
            }
        }
        // 先释放MediaPlayer
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mCurrentIndex = index;
        mCurrentProgress = 0;
        mPlaying = true;
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(music.getFilePath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            startTimer();
            if (mListener != null) {
                mListener.onPlay();
            }
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playNext();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (mPlaying) {
            mMediaPlayer.pause();
            mPlaying = false;
            if (mListener != null) {
                mListener.onPause();
            }
        }
    }

    public void resume() {
        if (!mPlaying) {
            mMediaPlayer.start();
            if (mListener != null) {
                mListener.onResume();
            }
        }
    }

    public void playNext() {
        play(mCurrentIndex + 1);
        if (mListener != null){
            mListener.onPlayNext();
        }
    }

    public void playPrev() {
        play(mCurrentIndex - 1);
        if (mListener != null){
            mListener.onPlayPrev();
        }
    }


    public List<Music> getPlayList() {
        return mPlayList;
    }

    public void setPlayList(List<Music> playList) {
        mPlayList = playList;
    }

    public boolean isPlaying() {
        return mPlaying;
    }

    public void setPlaying(boolean playing) {
        mPlaying = playing;
    }

    public int getCurrentProgress() {
        return mCurrentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        mCurrentProgress = currentProgress;
        mMediaPlayer.seekTo(mCurrentProgress);
    }

    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        mCurrentIndex = currentIndex;
    }

    public PlayerListener getListener() {
        return mListener;
    }

    public void setListener(PlayerListener listener) {
        mListener = listener;
    }

    public int getTotalTime() {
        return mMediaPlayer.getDuration();
    }

    private void startTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mPlaying) {
                    mCurrentProgress = mMediaPlayer.getCurrentPosition();
                    if (mListener != null) {
                        mListener.onProgressUpdate(mCurrentProgress);
                    }
                }
            }
        }, 0, 1000);
    }
}
