package vn.codefresher.musicplayer;

import java.io.Serializable;

public class MusicModel implements Serializable {

    private String name;
    private String url;
    private String album;
    private String thumbUrl;
    private long duration;

    public MusicModel(String name, String url, String album, String thumbUrl, long duration) {
        this.name = name;
        this.url = url;
        this.album = album;
        this.thumbUrl = thumbUrl;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getAlbum() {
        return album;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public long getDuration() {
        return duration;
    }
}
