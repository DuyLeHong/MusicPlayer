package vn.codefresher.musicplayer;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

final public class Utils {

    public static List<MusicModel> loadMusics() {
//        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
//
        List<MusicModel> data = new ArrayList<>();
//        Cursor cursor = getContentResolver().query(allsongsuri, null, null, null, selection);
//
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                String name = cursor.getString(cursor
//                                .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
//                String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
//                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
//                MusicModel model = new MusicModel(name, url, null, null, Long.valueOf(duration));
//                data.add(model);
//
//            } while (cursor.moveToNext());
//        }
//        cursor.close();

        ArrayList<HashMap<String, String>> songList = getMusicInFolder(Environment.getExternalStorageDirectory().getAbsolutePath());
        if (songList != null) {
            for (int i = 0; i < songList.size(); i++) {
                String fileName = songList.get(i).get("file_name");
                String filePath = songList.get(i).get("file_path");
                MusicModel model = new MusicModel(fileName, filePath, null, null, 0);
                data.add(model);

            }
        }

        return data;
    }

    private static ArrayList<HashMap<String, String>> getMusicInFolder(String rootPath) {
        ArrayList<HashMap<String, String>> fileList = new ArrayList<>();

        try {
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles(); //here you will get NPE if directory doesn't contains  any file,handle it like this.
            for (File file : files) {
                if (file.isDirectory()) {
                    if (getMusicInFolder(file.getAbsolutePath()) != null) {
                        fileList.addAll(getMusicInFolder(file.getAbsolutePath()));
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(".mp3")) {
                    HashMap<String, String> song = new HashMap<>();
                    song.put("file_path", file.getAbsolutePath());
                    song.put("file_name", file.getName());
                    fileList.add(song);
                }
            }
            return fileList;
        } catch (Exception e) {
            return null;
        }
    }
}
