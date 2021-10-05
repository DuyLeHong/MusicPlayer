package vn.codefresher.musicplayer;


import java.util.ArrayList;
import java.util.List;

public class MusicManager {

    private static MusicManager instance;
    private List<MusicModel> musicModels = new ArrayList<>();


    private MusicManager() {

    }

    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }

        return instance;
    }

    public void loadMusics() {
        List<MusicModel> musicModels = Utils.loadMusics();
        this.musicModels.addAll(musicModels);
    }

    public List<MusicModel> getMusics() {
        return musicModels;
    }
}
