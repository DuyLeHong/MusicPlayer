package vn.codefresher.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MusicPlayerService extends Service {
    public static final String ACTION_LOAD_MUSIC_DONE = "vn.codefresher.musicplayer.load_music_done";
    public static final String ACTION_START_LOAD_MUSIC = "vn.codefresher.musicplayer.start_load_music";
    public static final String ACTION_PLAY_MUSIC = "vn.codefresher.musicplayer.play_music";
    public static final String ACTION_PLAY_PREVIOUS_MUSIC = "vn.codefresher.musicplayer.play_previous_music";
    public static final String ACTION_UPDATE_STATUS = "vn.codefresher.musicplayer.update_status";

    public static final String MUSIC_POSITION_KEY = "position_key";
    public static final String IS_MUSIC_PLAYING_KEY = "is_playing_key";
    public static final String CANT_MOVE_NEXT_KEY = "can_move_next_key";
    public static final String CANT_MOVE_PREVIOUS_KEY = "can_move_previous_key";
    public static final String PLAYING_SONG_KEY = "playing_song_key";

    private MusicManager musicManager;
    private final MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler handler;

    private int currentPosition = -1;

    private final Runnable statusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                updateStatus(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                // 5 seconds by default, can be changed later
                int interval = 1000;
                handler.postDelayed(statusChecker, interval);
            }
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        musicManager = MusicManager.getInstance();
        handler = new Handler();
        startRepeatingTask();
    }

    private void startRepeatingTask() {
        statusChecker.run();
    }

    void stopRepeatingTask() {
        handler.removeCallbacks(statusChecker);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        switch (action) {
            case ACTION_START_LOAD_MUSIC: {
                loadMusics();
                break;
            }
            case ACTION_PLAY_MUSIC: {
                int position = intent.getIntExtra(MUSIC_POSITION_KEY, 0);
                playMusic(position);
                break;
            }
            case ACTION_PLAY_PREVIOUS_MUSIC: {
                previous();
                break;
            }
            default: {
                break;
            }
        }


        return super.onStartCommand(intent, flags, startId);
    }


    private void loadMusics() {
        musicManager.loadMusics();
        Intent intent = new Intent(ACTION_LOAD_MUSIC_DONE);
        sendBroadcast(intent);
    }

    private void playMusic(int position) {
        MusicModel model = musicManager.getMusics().get(position);
        currentPosition = position;
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(model.getUrl());
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void next() {
        if (currentPosition == musicManager.getMusics().size() - 1) {
            return;
        }
        playMusic(currentPosition + 1);
    }

    private void previous() {
        if (currentPosition == 0) {
            return;
        }
        playMusic(currentPosition -1);
    }

    private void updateStatus() {
        if (currentPosition == -1) {
            return;
        }
        Intent intent = new Intent(ACTION_UPDATE_STATUS);
        Bundle bundle = new Bundle();

        bundle.putBoolean(IS_MUSIC_PLAYING_KEY, mediaPlayer.isPlaying());
        bundle.putBoolean(CANT_MOVE_PREVIOUS_KEY, currentPosition > 0);
        bundle.putBoolean(CANT_MOVE_NEXT_KEY, currentPosition < musicManager.getMusics().size() - 1);
        bundle.putSerializable(PLAYING_SONG_KEY, musicManager.getMusics().get(currentPosition));
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

}
