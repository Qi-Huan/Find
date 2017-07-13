package com.qihuan.find.view;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qihuan.find.R;
import com.qihuan.find.bean.zhihu.DailyEntity;
import com.qihuan.find.bean.zhihu.DailyItem;
import com.qihuan.find.bean.zhihu.StoriesEntity;
import com.qihuan.find.bean.zhihu.TopStoriesEntity;
import com.qihuan.find.kit.DateKit;
import com.qihuan.find.kit.ToastKit;
import com.qihuan.find.presenter.NewsPresenter;
import com.qihuan.find.view.adapter.DailyAdapter;
import com.qihuan.find.view.base.BaseFragment;
import com.qihuan.find.view.i.INewsView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import easymvp.annotation.FragmentView;
import easymvp.annotation.Presenter;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * NewsFragment
 */
@FragmentView(presenter = NewsPresenter.class)
public class NewsFragment extends BaseFragment implements INewsView,
        SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.OnItemClickListener,
        BaseQuickAdapter.RequestLoadMoreListener {

    @Presenter
    NewsPresenter newsPresenter;

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView rvList;
    private List<TopStoriesEntity> topStories = new ArrayList<>();
    private List<DailyItem> stories = new ArrayList<>();
    private DailyAdapter dailyAdapter;
    private String date = DateKit.getNowDate();

    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        rvList = (RecyclerView) view.findViewById(R.id.rv_list);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);

        refreshLayout.setOnRefreshListener(this);
        dailyAdapter = new DailyAdapter(stories);
        dailyAdapter.setOnItemClickListener(this);
        dailyAdapter.setOnLoadMoreListener(this, rvList);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setAdapter(dailyAdapter);

        Observable.timer(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        newsPresenter.getLatestDaily();
                    }
                });

    }

    @Override
    public void start() {

    }

    @Override
    public void end() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void error(String message) {
        refreshLayout.setRefreshing(false);
        ToastKit.error(message);
    }

    @Override
    public void topDaily(DailyEntity dailyEntity) {
        topStories.clear();
        topStories.addAll(dailyEntity.getTop_stories());
        stories.clear();
        stories.add(new DailyItem(true, "今日热闻"));
        for (StoriesEntity storiesEntity : dailyEntity.getStories()) {
            stories.add(new DailyItem(storiesEntity));
        }
        dailyAdapter.notifyDataSetChanged();
    }

    @Override
    public void beforeDaily(DailyEntity dailyEntity) {
        stories.add(new DailyItem(true, DateKit.parseDate(dailyEntity.getDate())));
        for (StoriesEntity storiesEntity : dailyEntity.getStories()) {
            stories.add(new DailyItem(storiesEntity));
        }
        dailyAdapter.notifyDataSetChanged();
        dailyAdapter.loadMoreComplete();
    }

    @Override
    public void onRefresh() {
        date = DateKit.getNowDate();
        newsPresenter.getLatestDaily();
    }

    @Override
    public void onLoadMoreRequested() {
        date = DateKit.timeSub(date);
        newsPresenter.getBeforeDaily(date);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        DailyItem dailyItem = stories.get(position);
        if (dailyItem.isHeader) {
            return;
        }
        if (dailyItem.t == null) {
            return;
        }
        ToastKit.success(dailyItem.t.getTitle());
    }
}
