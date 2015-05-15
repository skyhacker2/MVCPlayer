package com.eleven.app.mvcplayer;

import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eleven.app.mvcplayer.models.Music;
import com.eleven.app.mvcplayer.models.MusicPlayer;

import java.util.ArrayList;
import java.util.List;


public class MusicListActivity extends ActionBarActivity {

    private ListView mMusicListView;
    private ImageButton mPlayButton;
    private ImageButton mPreButton;
    private ImageButton mNextButton;
    private SeekBar mSeekBar;
    private TextView mCurrentPlayTime;
    private TextView mMusicTotalTime;

    private Cursor mCursor;
    private MusicListAdapter mMusicListAdapter;
    private MusicPlayer mMusicPlayer;
    private List<Music> mPlayList;
    private MusicPlayer.PlayerListener mPlayerListener;
    private int mSeekPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        mMusicListView = (ListView)findViewById(R.id.music_list);
        mPlayButton = (ImageButton)findViewById(R.id.play_button);
        mPreButton = (ImageButton)findViewById(R.id.pre_button);
        mNextButton = (ImageButton)findViewById(R.id.next_button);
        mSeekBar = (SeekBar)findViewById(R.id.list_music_seekBar);
        mCurrentPlayTime = (TextView) findViewById(R.id.list_current_play_time);
        mMusicTotalTime = (TextView) findViewById(R.id.list_music_totalTime);

        mMusicPlayer = MusicPlayer.getInstance();
        mMusicPlayer.setPlayList(getPlayList());
        mMusicPlayer.setListener(getPlayerListener());

        mMusicListAdapter = new MusicListAdapter(this, getPlayList());
        mMusicListView.setAdapter(mMusicListAdapter);
        mMusicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMusicPlayer.play(position);
            }
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMusicPlayer.isPlaying()) {
                    mMusicPlayer.pause();
                } else {
                    mMusicPlayer.resume();
                }
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMusicPlayer.playNext();
            }
        });

        mPreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMusicPlayer.playPrev();
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mSeekPosition = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMusicPlayer.setCurrentProgress(mSeekPosition);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_music_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int playingIndex = mMusicPlayer.getCurrentIndex();
                mMusicListAdapter.setSelectedIndex(playingIndex);
                mMusicListAdapter.notifyDataSetChanged();

                if (mMusicPlayer.isPlaying()) {
                    mPlayButton.setImageResource(R.drawable.selector_pause_button);
                } else {
                    mPlayButton.setImageResource(R.drawable.selector_play_button);
                }

                int progress = mMusicPlayer.getCurrentProgress();
                mSeekBar.setMax(mMusicPlayer.getTotalTime());
                mSeekBar.setProgress(progress);

                mCurrentPlayTime.setText(formatTime(progress));
                mMusicTotalTime.setText(formatTime(mMusicPlayer.getTotalTime()));
            }
        });
    }


    public List<Music> getPlayList() {
        // 惰性初始化
        if (mPlayList != null) {
            return mPlayList;
        }
        mPlayList = new ArrayList<>();
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        // 查询音乐
        mCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,new String[] { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.DATA }, null, null, null);

        if (mCursor == null || mCursor.moveToFirst() == false) {
            Toast.makeText(this, "没有音乐", Toast.LENGTH_LONG).show();
            return mPlayList;
        }
        int index = 0;
        mCursor.moveToFirst();
        while (mCursor.moveToNext()) {
            Music song = new Music();
            song.setMusicId(index);
            song.setFileName(mCursor.getString(1));
            song.setMusicName(mCursor.getString(2));
            song.setMusicDuration(mCursor.getInt(3));
            song.setMusicArtist(mCursor.getString(4));
            song.setMusicAlbum(mCursor.getString(5));
            song.setMusicYear(mCursor.getString(6));
            // file type
            if ("audio/mpeg".equals(mCursor.getString(7).trim())) {
                song.setFileType("mp3");
            } else if ("audio/x-ms-wma".equals(mCursor.getString(7).trim())){
                song.setFileType("wma");
            }
            song.setFileType(mCursor.getString(7));
            song.setFileSize(mCursor.getString(8));
            if (mCursor.getString(9) != null) {
                song.setFilePath(mCursor.getString(9));
            }
            index++;
            mPlayList.add(song);
        }
        mCursor.close();
        mCursor = null;
        return mPlayList;
    }

    public MusicPlayer.PlayerListener getPlayerListener() {
        if (mPlayerListener == null) {
            mPlayerListener = new MusicPlayer.PlayerListener() {
                @Override
                public void onPlay() {
                    updateUI();
                }

                @Override
                public void onPause() {
                    updateUI();
                }

                @Override
                public void onResume() {
                    updateUI();
                }

                @Override
                public void onPlayNext() {
                    updateUI();
                }

                @Override
                public void onPlayPrev() {
                    updateUI();
                }

                @Override
                public void onProgressUpdate(int progress) {
                    updateUI();
                }
            };
        }
        return mPlayerListener;
    }

    private String formatTime(int time) {
        time /= 1000;
        int hour;
        int minute;
        int second;
        if (time > 3600) {
            hour = time / 3600;
            minute = (time % 3600) / 60;
            second = (time % 3600) % 60;
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            minute = time / 60;
            second = time % 60;
            return String.format("%02d:%02d", minute, second);
        }
    }
}
