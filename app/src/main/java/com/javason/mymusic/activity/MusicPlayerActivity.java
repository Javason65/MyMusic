package com.javason.mymusic.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.javason.mymusic.R;
import com.javason.mymusic.domain.Song;
import com.javason.mymusic.listener.OnMusicPlayerListener;
import com.javason.mymusic.manager.MusicPlayerManager;
import com.javason.mymusic.service.MusicPlayerService;
import com.javason.mymusic.util.AlbumDrawableUtil;
import com.javason.mymusic.util.ImageUtil;
import com.javason.mymusic.util.TimeUtil;
import com.javason.mymusic.view.ListLyricView;
import com.javason.mymusic.view.RecordThumbView;
import com.javason.mymusic.view.RecordView;

import org.apache.commons.lang3.StringUtils;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class MusicPlayerActivity extends BaseCommonActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, OnMusicPlayerListener {
    private ImageView iv_loop_model;
    private ImageView iv_album_bg;
    private ImageView iv_play_control;
    private ImageView iv_play_list;
    private ImageView iv_previous;
    private ImageView iv_next;
    private TextView tv_start_time;
    private TextView tv_end_time;
    private SeekBar sb_progress;
    private RecordThumbView rt;
    private ImageView iv_download;
    private RecordView rv;
    private LinearLayout lyric_container;
    private RelativeLayout rl_player_container;
    private SeekBar sb_volume;
    private ListLyricView lv;
    private boolean isPlayer;
    private MusicPlayerManager musicPlayerManager;


    private ViewPager vp;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        musicPlayerManager = MusicPlayerService.getMusicPlayerManager(getApplicationContext());
        //音量
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        setVolume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        musicPlayerManager.addOnMusicPlayerListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        musicPlayerManager.removeOnMusicPlayerListener(this);
    }

    private void setVolume() {
        int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        sb_volume.setMax(max);
        sb_volume.setProgress(current);
    }

    @Override
    protected void initViews() {
        super.initViews();
        enableBackMenu();
        rv = findViewById(R.id.rv);
        iv_download = findViewById(R.id.iv_download);
        iv_album_bg = findViewById(R.id.iv_album_bg);
        iv_loop_model = findViewById(R.id.iv_loop_model);
        iv_play_control = findViewById(R.id.iv_play_control);
        rt = findViewById(R.id.rt);
        tv_start_time = findViewById(R.id.tv_start_time);
        tv_end_time = findViewById(R.id.tv_end_time);
        sb_progress = findViewById(R.id.sb_progress);
        iv_next = findViewById(R.id.iv_next);
        iv_previous = findViewById(R.id.iv_previous);
        iv_play_list = findViewById(R.id.iv_play_list);
        //rv = findViewById(R.id.rv);
        lyric_container = findViewById(R.id.lyric_container);
        rl_player_container = findViewById(R.id.rl_player_container);
        sb_volume = findViewById(R.id.sb_volume);
        lv = findViewById(R.id.lv);

        vp = findViewById(R.id.vp);
    }

    @Override
    protected void initListener() {
        super.initListener();
        iv_download.setOnClickListener(this);
        iv_play_control.setOnClickListener(this);
        iv_play_list.setOnClickListener(this);
        iv_loop_model.setOnClickListener(this);
        iv_previous.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        sb_progress.setOnSeekBarChangeListener(this);

        //由于歌词控件内部使用了RecyclerView
        //直接给ListLyricView设置点击，长按
        //事件是无效的，因为内部的RecyclerView拦截了
        //解决方法是监听Item点击，然后通过接口回调（当然也可以使用EventBus）回来
        //rv.setOnClickListener(this);
        //lv.setOnClickListener(this);
        sb_volume.setOnSeekBarChangeListener(this);

        //lv.setOnLongClickListener(this);
        //rv.setOnLongClickListener(this);

//        lv.setLyricListener(this);
//
//        lv.setOnLyricClickListener(this);
//        playListManager.addPlayListListener(this);
//
//        vp.addOnPageChangeListener(this);
    }

    private void stopRecordRotate() {
        rv.stopAlbumRotate();
//        EventBus.getDefault().post(new OnStopRecordEvent(currentSong));
        rt.stopThumbAnimation();
    }

    private void startRecordRotate() {
        rv.startAlbumRotate();
//        EventBus.getDefault().post(new OnStartRecordEvent(currentSong));
        rt.startThumbAnimation();
    }

    protected void enableBackMenu() {
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play_control:
                playOrPause();
                break;
//            case R.id.iv_play_list:
//                showPlayListDialog();
//                break;
//            //case R.id.lv:
//            //    showRecordView();
//            //    break;
//            //case R.id.rv:
//            //    showLyricView();
//            //    break;
//            case R.id.iv_previous:
//                Song song = playListManager.previous();
//                playListManager.play(song);
//                break;
//            case R.id.iv_next:
//                Song songNext = playListManager.next();
//                playListManager.play(songNext);
//                break;
//            case R.id.iv_loop_model:
//                int loopModel = playListManager.changeLoopModel();
//                showLoopModel(loopModel);
//                break;
//            case R.id.iv_download:
//                download();
//                break;
        }
    }

    private void playOrPause() {
        if (isPlayer) {
            pause();
        } else {
            play();
        }

        isPlayer = !isPlayer;

    }

    private void play() {
//        playListManager.resume();

        musicPlayerManager.play("http://dev-courses-misuc.ixuea.com/assets/yiki/dont_give.mp3",new Song());
    }

    private void pause() {
//        playListManager.pause();
        stopRecordRotate();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            if (seekBar.getId() == R.id.sb_volume) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            } else {
                musicPlayerManager.seekTo(progress);
                if (!musicPlayerManager.isPlaying()) {
                    musicPlayerManager.resume();
                }
            }

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onProgress(long progress, long total) {
        tv_start_time.setText(TimeUtil.formatMSTime((int) progress));
        sb_progress.setProgress((int) progress);
//        lv.show(progress);
    }

    @Override
    public void onPaused(Song data) {
        iv_play_control.setImageResource(R.drawable.selector_music_play);
        stopRecordRotate();
    }

    @Override
    public void onPlaying(Song data) {
        iv_play_control.setImageResource(R.drawable.selector_music_pause);
        startRecordRotate();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer, Song data) {
        setFirstData(data);
    }

    public void setFirstData(Song data) {
        sb_progress.setMax((int) data.getDuration());
        sb_progress.setProgress(sp.getLastSongProgress());
        tv_start_time.setText(TimeUtil.formatMSTime((int) sp.getLastSongProgress()));
        tv_end_time.setText(TimeUtil.formatMSTime((int) data.getDuration()));

        //rv.setAlbumUri(data.getBanner());
        getActivity().setTitle(data.getTitle());

//        getActivity().getSupportActionBar().setSubtitle(data.getArtist_name());

        if (StringUtils.isNotBlank(data.getBanner())) {
            //ImageUtil.showImageBlur(getActivity(), iv_album_bg, data.getBanner());
            final RequestOptions requestOptions = bitmapTransform(new BlurTransformation(50, 5));

            //requestOptions.placeholder(R.drawable.default_album);
            requestOptions.error(R.drawable.default_album);
            Glide.with(getActivity()).asDrawable().load(ImageUtil.getImageURI(data.getBanner())).apply(requestOptions).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    AlbumDrawableUtil albumDrawableUtil = new AlbumDrawableUtil(iv_album_bg.getDrawable(), resource);
                    iv_album_bg.setImageDrawable(albumDrawableUtil.getDrawable());
                    albumDrawableUtil.start();
                }
            });
        }

        //if (data.getLyric() != null && StringUtils.isNotBlank(data.getLyric().getContent())) {
        //    fetchLyric();
        //} else {
        //    //直接设置歌词信息，存在于本地
        //    //setLyric();
        //}

//        scrollToCurrentSongPosition(currentSong);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onError(MediaPlayer mp, int what, int extra) {

    }
}
