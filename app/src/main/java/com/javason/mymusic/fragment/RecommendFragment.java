package com.javason.mymusic.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.javason.mymusic.R;
import com.javason.mymusic.adapter.BaseRecyclerViewAdapter;
import com.javason.mymusic.adapter.RecommendAdapter;
import com.javason.mymusic.api.Api;
import com.javason.mymusic.domain.List;
import com.javason.mymusic.domain.Advertisement;
import com.javason.mymusic.domain.Song;
import com.javason.mymusic.domain.response.ListResponse;
import com.javason.mymusic.reactivex.HttpListener;

import com.javason.mymusic.util.DataUtil;

import java.util.ArrayList;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RecommendFragment extends BaseCommonFragment {

    private RecyclerView rv;
    private GridLayoutManager layoutManager;
    private RecommendAdapter adapter;

    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recommend,null);
    }
    public static RecommendFragment newInstance() {

        Bundle args = new Bundle();

        RecommendFragment fragment = new RecommendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initViews() {
        super.initViews();
        rv=findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(getActivity(), 3);
        rv.setLayoutManager(layoutManager);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        adapter=new RecommendAdapter(getActivity());
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {

            }
        });

        rv.setAdapter(adapter);
        fetchData();
    }

    private void fetchData() {
        //这里获取三种类型的数据，然后放到一个列表中
        //同时也是演示RecyclerView不同的ItemType的使用方法
        //详细的使用方法可以在我们的《详解RecyclerView》课程中学到

        Observable<ListResponse<List>> list = Api.getInstance().lists();
        final Observable<ListResponse<Song>> songs = Api.getInstance().songs();
        final Observable<ListResponse<Advertisement>> advertisements = Api.getInstance().advertisements();

        final ArrayList<Object> d = new ArrayList<>();
        d.add("推荐歌单");

        //为降低课程难度，不使用RxJava来合并请求
        list.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<ListResponse<List>>(getMainActivity()) {
                    @Override
                    public void onSucceeded(final ListResponse<List> data) {
                        super.onSucceeded(data);
                        d.addAll(data.getData());

                        songs.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new HttpListener<ListResponse<Song>>(getMainActivity()) {
                                    @Override
                                    public void onSucceeded(ListResponse<Song> data) {
                                        super.onSucceeded(data);
                                        d.add("推荐单曲");
                                        d.addAll(DataUtil.fill(data.getData()));

                                        advertisements.subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new HttpListener<ListResponse<Advertisement>>(getMainActivity()){
                                                    @Override
                                                    public void onSucceeded(ListResponse<Advertisement> data) {
                                                        super.onSucceeded(data);
                                                        d.addAll(data.getData());

                                                        adapter.setData(d);
                                                        //rv.refreshComplete(Consts.DEFAULT_PAGE_SIZE);
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

}
