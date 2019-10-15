package com.javason.mymusic.manager;

import com.javason.mymusic.domain.Song;
import com.javason.mymusic.listener.PlayListListener;

import java.util.List;

public interface PlayListManager {
    List<Song> getPlayList();

    void setPlayList(List<Song> datum);

    void play(Song song);

    void pause();

    void resume();

    void delete(Song song);

    Song getPlayData();

    Song next();

    Song previous();

    int getLoopModel();

    int changeLoopModel();

    void addPlayListListener(PlayListListener listener);

    void removePlayListListener(PlayListListener listener);

    void destroy();

    /**
     * 下一首播放
     * @param song
     */
    void nextPlay(Song song);
}
