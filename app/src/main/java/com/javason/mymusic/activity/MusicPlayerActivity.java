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
import com.javason.mymusic.domain.Lyric;
import com.javason.mymusic.domain.Song;
import com.javason.mymusic.listener.OnMusicPlayerListener;
import com.javason.mymusic.manager.MusicPlayerManager;
import com.javason.mymusic.parser.LyricsParser;
import com.javason.mymusic.service.MusicPlayerService;
import com.javason.mymusic.util.AlbumDrawableUtil;
import com.javason.mymusic.util.ImageUtil;
import com.javason.mymusic.util.TimeUtil;
import com.javason.mymusic.view.LyricView;
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
    private LyricView lv;
    private boolean isPlayer;
    private MusicPlayerManager musicPlayerManager;


    private ViewPager vp;
    private AudioManager audioManager;
    private LyricsParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
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
    protected void initDatas() {
        super.initDatas();
        musicPlayerManager = MusicPlayerService.getMusicPlayerManager(getApplicationContext());
        //音量
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        setVolume();

        musicPlayerManager.play("http://dev-courses-misuc.ixuea.com/assets/yiki/dont_give.mp3",new Song());
        Lyric lyric=new Lyric();
        lyric.setStyle(10);
        lyric.setContent("karaoke := CreateKaraokeObject;\nkaraoke.rows := 2;\nkaraoke.TimeAfterAnimate := 2000;\nkaraoke.TimeBeforeAnimate := 4000;\nkaraoke.clear;\nkaraoke.add('00:20.699', '00:27.055', '[●●●●●●]', '7356',RGB(255,0,0));\n\nkaraoke.add('00:27.487', '00:32.068', '一时失志不免怨叹', '347,373,1077,320,344,386,638,1096');\nkaraoke.add('00:33.221', '00:38.068', '一时落魄不免胆寒', '282,362,1118,296,317,395,718,1359');\nkaraoke.add('00:38.914', '00:42.164', '那通失去希望', '290,373,348,403,689,1147');\nkaraoke.add('00:42.485', '00:44.530', '每日醉茫茫', '298,346,366,352,683');\nkaraoke.add('00:45.273', '00:49.029', '无魂有体亲像稻草人', '317,364,380,351,326,351,356,389,922');\nkaraoke.add('00:50.281', '00:55.585', '人生可比是海上的波浪', '628,1081,376,326,406,371,375,1045,378,318');\nkaraoke.add('00:56.007', '01:00.934', '有时起有时落', '303,362,1416,658,750,1438');\nkaraoke.add('01:02.020', '01:04.581', '好运歹运', '360,1081,360,760');\nkaraoke.add('01:05.283', '01:09.453', '总嘛要照起来行', '303,338,354,373,710,706,1386');\nkaraoke.add('01:10.979', '01:13.029', '三分天注定', '304,365,353,338,690');\nkaraoke.add('01:13.790', '01:15.950', '七分靠打拼', '356,337,338,421,708');\nkaraoke.add('01:16.339', '01:20.870', '爱拼才会赢', '325,1407,709,660,1430');\nkaraoke.add('01:33.068', '01:37.580', '一时失志不免怨叹', '307,384,1021,363,357,374,677,1029');\nkaraoke.add('01:38.660', '01:43.656', '一时落魄不免胆寒', '381,411,1067,344,375,381,648,1389');\nkaraoke.add('01:44.473', '01:47.471', '那通失去希望', '315,365,340,369,684,925');\nkaraoke.add('01:48.000', '01:50.128', '每日醉茫茫', '338,361,370,370,689');\nkaraoke.add('01:50.862', '01:54.593', '无魂有体亲像稻草人', '330,359,368,376,325,334,352,389,898');\nkaraoke.add('01:55.830', '02:01.185', '人生可比是海上的波浪', '654,1056,416,318,385,416,373,1032,342,363');\nkaraoke.add('02:01.604', '02:06.716', '有时起有时落', '303,330,1432,649,704,1694');\nkaraoke.add('02:07.624', '02:10.165', '好运歹运', '329,1090,369,753');\nkaraoke.add('02:10.829', '02:15.121', '总嘛要照起来行', '313,355,362,389,705,683,1485');\nkaraoke.add('02:16.609', '02:18.621', '三分天注定', '296,363,306,389,658');\nkaraoke.add('02:19.426', '02:21.428', '七分靠打拼', '330,359,336,389,588');\nkaraoke.add('02:21.957', '02:26.457', '爱拼才会赢', '315,1364,664,767,1390');\nkaraoke.add('02:50.072', '02:55.341', '人生可比是海上的波浪', '656,1086,349,326,359,356,364,1095,338,340');\nkaraoke.add('02:55.774', '03:01.248', '有时起有时落', '312,357,1400,670,729,2006');\nkaraoke.add('03:01.787', '03:04.369', '好运歹运', '341,1084,376,781');\nkaraoke.add('03:05.041', '03:09.865', '总嘛要起工来行', '305,332,331,406,751,615,2084');\nkaraoke.add('03:10.754', '03:12.813', '三分天注定', '309,359,361,366,664');\nkaraoke.add('03:13.571', '03:15.596', '七分靠打拼', '320,362,349,352,642');\nkaraoke.add('03:16.106', '03:20.688', '爱拼才会赢', '304,1421,661,706,1490');\n");

        setLyric(lyric);
    }

    private void setLyric(Lyric lyric) {
        parser = LyricsParser.parse(lyric.getStyle(), lyric.getContent());
        parser.parse();
        if (parser.getLyric() != null) {
            lv.setData(parser.getLyric());
        }
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
        rv.setOnClickListener(this);
        lv.setOnClickListener(this);
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
            case R.id.lv:
                lyric_container.setVisibility(View.GONE);
                rl_player_container.setVisibility(View.VISIBLE);
                break;
            case R.id.rv:
                lyric_container.setVisibility(View.VISIBLE);
                rl_player_container.setVisibility(View.GONE);
                System.out.println("onclick");
                break;
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

    private void showRecordView() {
        lyric_container.setVisibility(View.GONE);
        rl_player_container.setVisibility(View.VISIBLE);
    }

    private void showLyricView() {
        lyric_container.setVisibility(View.VISIBLE);
        rl_player_container.setVisibility(View.GONE);
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

       musicPlayerManager.resume();
    }

    private void pause() {
//        playListManager.pause();
        musicPlayerManager.pause();
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
        lv.show(progress);
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

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onError(MediaPlayer mp, int what, int extra) {

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


}
