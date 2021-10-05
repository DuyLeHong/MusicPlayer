package vn.codefresher.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import static vn.codefresher.musicplayer.MusicPlayerService.ACTION_LOAD_MUSIC_DONE;
import static vn.codefresher.musicplayer.MusicPlayerService.ACTION_PLAY_MUSIC;
import static vn.codefresher.musicplayer.MusicPlayerService.ACTION_START_LOAD_MUSIC;
import static vn.codefresher.musicplayer.MusicPlayerService.ACTION_UPDATE_STATUS;
import static vn.codefresher.musicplayer.MusicPlayerService.IS_MUSIC_PLAYING_KEY;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_WRITE_STORAGE = 1;
    public static final String MINI_CONTROLLER_TAG = "MINI_CONTROLLER_TAG";

    private ProgressBar progressBar;
    private MusicAdapter adapter;

    private MusicsReceiver receiver = new MusicsReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupView();
        checkPermission();
        registerReceiver();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterReceiver();
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter(ACTION_LOAD_MUSIC_DONE);
        intentFilter.addAction(ACTION_UPDATE_STATUS);
        registerReceiver(receiver, intentFilter);
    }

    private void unRegisterReceiver() {
        unregisterReceiver(receiver);
    }

    private void setupView() {
        progressBar = findViewById(R.id.progressBar);

        RecyclerView recyclerView = findViewById(R.id.rvMusicList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MusicAdapter(new MusicAdapter.Callback() {
            @Override
            public void onClickItem(int position) {
                onMusicClick(position);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private boolean hasStoragePermission() {
        int permission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    private void checkPermission() {
        boolean hasPermission = hasStoragePermission();
        if (hasPermission) {
            loadMusics();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadMusics();
        }
    }

    public void loadMusics() {
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction(ACTION_START_LOAD_MUSIC);
        startService(intent);
    }

    public void onMusicClick(int position) {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction(ACTION_PLAY_MUSIC);
        intent.putExtra(MusicPlayerService.MUSIC_POSITION_KEY, position);
        startService(intent);
    }

    class MusicsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_LOAD_MUSIC_DONE.equals(intent.getAction())){
                progressBar.setVisibility(View.GONE);
                adapter.setData(MusicManager.getInstance().getMusics());
            } else {
                Bundle bundle = intent.getExtras();
                boolean isPlaying = bundle.getBoolean(IS_MUSIC_PLAYING_KEY, false);
                if (isPlaying) {
                    addFragmentIfNeed();
                } else {
                    removeFragmentIfNeed();
                }
            }

        }
    }

    private void addFragmentIfNeed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(MINI_CONTROLLER_TAG) == null){
            MusicControlFragment fragment = MusicControlFragment.newInstance(true);
            fm.beginTransaction()
                    .add(R.id.smallController, fragment, MINI_CONTROLLER_TAG)
                    .commit();
        }
    }

    private void removeFragmentIfNeed() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(MINI_CONTROLLER_TAG);
        if (fragment != null){
            fm.beginTransaction().remove(fragment);
        }
    }
}