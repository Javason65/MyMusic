package com.javason.mymusic.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.javason.mymusic.R;
import com.javason.mymusic.util.Consts;

import org.apache.commons.lang3.StringUtils;

;

/**
 * Created by smile on 2018/6/22.
 */

public class FeedFragment extends BaseCommonFragment implements View.OnClickListener {
    @Override
    public void onClick(View v) {

    }

    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed,null);
    }

    public static FeedFragment newInstance(String userId) {

        Bundle args = new Bundle();
        if (StringUtils.isNotBlank(userId)) {
            args.putString(Consts.ID,userId);
        }

        FeedFragment fragment = new FeedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static FeedFragment newInstance() {
        return newInstance(null);
    }

   /* private RecyclerView rv;
    private FeedAdapter adapter;
    private FloatingActionButton fab;
    private String userId;

    public static FeedFragment newInstance(String userId) {

        Bundle args = new Bundle();
        if (StringUtils.isNotBlank(userId)) {
            args.putString(Consts.ID,userId);
        }

        FeedFragment fragment = new FeedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static FeedFragment newInstance() {
        return newInstance(null);
    }

    @Override
    protected void initViews() {
        super.initViews();
        EventBus.getDefault().register(this);

        fab=findViewById(R.id.fab);

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
        userId = getArguments().getString(Consts.ID);

        if (StringUtils.isNotBlank(userId)) {
            //用户详情，隐藏FAB，当然可以根据业务细分，如：在主界面，我的用户界面才显示
            fab.setVisibility(View.GONE);
        }

        adapter = new FeedAdapter(getActivity(), R.layout.item_feed);
        adapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerViewAdapter.ViewHolder holder, int position) {

            }
        });
        rv.setAdapter(adapter);

        loadMore();
    }

    @Override
    protected void initListener() {
        super.initListener();
        fab.setOnClickListener(this);
    }


    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed,null);
    }


    private void loadMore() {
        Api.getInstance().feeds(userId, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpListener<ListResponse<Feed>>(getMainActivity()) {
                    @Override
                    public void onSucceeded(ListResponse<Feed> data) {
                        super.onSucceeded(data);
                        next(data.getData());
                    }
                });
    }

    public void next(List<Feed> data) {
        adapter.setData(data);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fab:
                startActivity(PublishMessageActivity.class);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void publishMessageEvent(PublishMessageEvent event) {
        loadMore();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }*/

}
