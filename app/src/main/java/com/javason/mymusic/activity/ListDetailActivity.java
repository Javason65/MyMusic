package com.javason.mymusic.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.javason.mymusic.R;
import com.javason.mymusic.adapter.BaseRecyclerViewAdapter;
import com.javason.mymusic.adapter.SongAdapter;
import com.javason.mymusic.api.Api;
import com.javason.mymusic.domain.List;
import com.javason.mymusic.domain.Song;
import com.javason.mymusic.domain.response.DetailResponse;
import com.javason.mymusic.reactivex.HttpListener;
import com.javason.mymusic.util.Consts;
import com.javason.mymusic.util.DataUtil;
import com.javason.mymusic.util.ImageUtil;

import org.apache.commons.lang3.StringUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ListDetailActivity extends BaseMusicPlayerActivity implements SongAdapter.OnSongListener, View.OnClickListener {
    private LRecyclerView rv;
    private ImageView iv_icon;
    private TextView tv_title;
    private TextView tv_nickname;
    private TextView tv_comment_count;
    private LinearLayout header_container;
    private LinearLayout ll_comment_container;
    private LinearLayout ll_play_all_container;
    private RelativeLayout rl_player_container;
    private TextView tv_play_all;
    private TextView tv_count;
    private Button bt_collection;

    private String id;
    private SongAdapter adapter;
    private LRecyclerViewAdapter adapterWrapper;
    private List data;
//    private PlayListManager playListManager;
//    private MusicPlayerManager musicPlayerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);
    }

    @Override
    protected void initViews() {
        super.initViews();
        enableBackMenu();

        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);

        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), RecyclerView.VERTICAL);
        rv.addItemDecoration(decoration);
    }


    @Override
    protected void initDatas() {
        super.initDatas();
        //已在父类中初始化
//        playListManager = MusicPlayerService.getPlayListManager(getApplicationContext());
//        musicPlayerManager = MusicPlayerService.getMusicPlayerManager(getApplicationContext());
        id = getIntent().getStringExtra(Consts.ID);


//        adapter = new SongAdapter(getActivity(), R.layout.item_song_list_detail, getSupportFragmentManager());
        adapter = new SongAdapter(getActivity(), R.layout.item_song_list_detail, getSupportFragmentManager(),playListManager);
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {
                play(position);
            }
        });
        adapter.setOnSongListener(this);

        adapterWrapper = new LRecyclerViewAdapter(adapter);

        adapterWrapper.addHeaderView(createHeaderView());

        rv.setAdapter(adapterWrapper);
        rv.setPullRefreshEnabled(false);
        rv.setLoadMoreEnabled(false);

        fetchData();
    }

    @Override
    protected void initListener() {
        super.initListener();
        ll_play_all_container.setOnClickListener(this);
        ll_comment_container.setOnClickListener(this);
        bt_collection.setOnClickListener(this);
    }

    private void play(int position) {
        Song data = adapter.getData(position);
        playListManager.setPlayList(adapter.getDatas());
        playListManager.play(data);
        adapter.notifyDataSetChanged();
        startActivity(MusicPlayerActivity.class);
    }

    private void fetchData() {
        Api.getInstance().listDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<DetailResponse<List>>(getActivity()) {
                    @Override
                    public void onSucceeded(DetailResponse<List> data) {
                        super.onSucceeded(data);
                        next(data.getData());
                    }
                });
    }

    @SuppressLint("ResourceType")
    private void next(List data) {
        this.data=data;
        RequestBuilder<Bitmap> bitmapRequestBuilder ;
        if (StringUtils.isBlank(data.getBanner())) {
            //如果头图为空，就用默认图片
            bitmapRequestBuilder = Glide.with(this).asBitmap().load(R.drawable.cd_bg);
        } else {
                bitmapRequestBuilder =Glide.with(this).asBitmap().load(ImageUtil.getImageURI(data.getBanner()));
        }
        bitmapRequestBuilder.into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull final Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(@NonNull Palette palette) {
                        iv_icon.setImageBitmap(resource);
                        Palette.Swatch swatch = palette.getVibrantSwatch();
                        if (swatch != null) {
                            int rgb = swatch.getRgb();
                            toolbar.setBackgroundColor(rgb);
                            header_container.setBackgroundColor(rgb);
                            //设置状态栏
                            if (android.os.Build.VERSION.SDK_INT >= 21) {
                                Window window = getWindow();
                                window.setStatusBarColor(rgb);
                                window.setNavigationBarColor(rgb);
                            }
                        }
                    }
                });
            }
        });


        tv_title.setText(data.getTitle());
        tv_nickname.setText(data.getUser().getNickname());
        tv_count.setText(getResources().getString(R.string.music_count, data.getSongs().size()));

        tv_comment_count.setText(String.valueOf(data.getComments_count()));

        if (data.isCollection()) {
            bt_collection.setText(R.string.cancel_collection_all);

            //已经收藏了，就需要弱化取消收藏按钮，因为我们的本质是想让用户收藏
            bt_collection.setBackground(null);
            bt_collection.setTextColor(getResources().getColor(R.color.text_grey));
        } else {
            bt_collection.setText(R.string.collection_all);
            bt_collection.setBackgroundResource(R.drawable.selector_button_reverse);
            bt_collection.setTextColor(getResources().getColorStateList(R.drawable.selector_text_reverse));
        }

//        ArrayList<Song> songs = new ArrayList<>();

        //添加测试数据；因为该接口返回的数据少
        //for (int i = 0; i < 100; i++) {
//        songs.addAll(DataUtil.fill(data.getSongs()));
        //}

        java.util.List<Song> songs = DataUtil.fill(data.getSongs());
        //playList.setPlayList(DataUtil.fill(data.getSongs()));

        boolean isMySheet = data.getUser().getId().equals(sp.getUserId());
        adapter.setMySheet(isMySheet);
        //adapter.setData(playList.getPlayList());
        adapter.setData(songs);

        //如果是自己的歌单，就没有收藏
        if (isMySheet) {
            bt_collection.setVisibility(View.GONE);
        }
    }


    private View createHeaderView() {
        View top = getLayoutInflater().inflate(R.layout.header_song_detail, (ViewGroup) rv.getParent(), false);
        header_container = top.findViewById(R.id.header_container);
        ll_comment_container = top.findViewById(R.id.ll_comment_container);
        bt_collection = top.findViewById(R.id.bt_collection);
        ll_play_all_container = top.findViewById(R.id.ll_play_all_container);
        iv_icon = top.findViewById(R.id.iv_icon);
        tv_title = top.findViewById(R.id.tv_title);
        tv_nickname = top.findViewById(R.id.tv_nickname);
        tv_comment_count = top.findViewById(R.id.tv_comment_count);
        tv_play_all = top.findViewById(R.id.tv_play_all);
        tv_count = top.findViewById(R.id.tv_count);

        return top;
    }

    @Override
    public void onCollectionClick(Song song) {

    }

    @Override
    public void onDownloadClick(Song song) {

    }

    @Override
    public void onDeleteClick(Song song) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_play_all_container:
                play(0);
                break;
            case R.id.bt_collection:
//                collectionList();
                break;
            case R.id.ll_comment_container:
//                Intent intent = new Intent(this, CommentListActivity.class);
//                intent.putExtra(Consts.LIST_ID,id);
//                intent.putExtra(Consts.STYLE,Consts.STYLE_LIST);
//                startActivity(intent);
//                break;
            default:
                super.onClick(v);
                break;
        }
    }
}
