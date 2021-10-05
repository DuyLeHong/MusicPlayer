package vn.codefresher.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static vn.codefresher.musicplayer.MusicPlayerService.ACTION_LOAD_MUSIC_DONE;
import static vn.codefresher.musicplayer.MusicPlayerService.ACTION_PLAY_MUSIC;
import static vn.codefresher.musicplayer.MusicPlayerService.ACTION_PLAY_PREVIOUS_MUSIC;
import static vn.codefresher.musicplayer.MusicPlayerService.ACTION_UPDATE_STATUS;
import static vn.codefresher.musicplayer.MusicPlayerService.IS_MUSIC_PLAYING_KEY;
import static vn.codefresher.musicplayer.MusicPlayerService.PLAYING_SONG_KEY;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MusicControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicControlFragment extends Fragment {
    private boolean isSmallController;

    private static final String CONTROLLER_TYPE_KEY = "CONTROLLER_TYPE";
    private MusicsReceiver receiver = new MusicsReceiver();

    private TextView tvTitle;


    public MusicControlFragment() {
        // Required empty public constructor
    }

    public static MusicControlFragment newInstance(boolean isSmallController) {
        MusicControlFragment fragment = new MusicControlFragment();
        Bundle args = new Bundle();
        args.putBoolean(CONTROLLER_TYPE_KEY, isSmallController);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isSmallController = getArguments().getBoolean(CONTROLLER_TYPE_KEY);
        }
        registerReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        int layout = isSmallController ? R.layout.fragment_music_control_small : R.layout.fragment_music_control;
        return inflater.inflate(layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
    }

    private void setupViews(View root) {
        tvTitle = root.findViewById(R.id.tvTittleMusic_detail);

        root.findViewById(R.id.imgButton_Previous_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPreviousClick();
            }
        });
    }

    public void onPreviousClick() {
        Intent intent = new Intent(getActivity(), MusicPlayerService.class);
        intent.setAction(ACTION_PLAY_PREVIOUS_MUSIC);
        getActivity().startService(intent);
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter(ACTION_LOAD_MUSIC_DONE);
        intentFilter.addAction(ACTION_UPDATE_STATUS);
        getActivity().registerReceiver(receiver, intentFilter);
    }

    private void unRegisterReceiver() {
        getActivity().unregisterReceiver(receiver);
    }


    class MusicsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            boolean isPlaying = bundle.getBoolean(IS_MUSIC_PLAYING_KEY, false);
            if (isPlaying) {
                MusicModel model = (MusicModel) bundle.get(PLAYING_SONG_KEY);
                tvTitle.setText(model.getName());
            }
        }
    }

}