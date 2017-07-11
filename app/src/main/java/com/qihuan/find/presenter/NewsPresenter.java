package com.qihuan.find.presenter;

import com.qihuan.find.bean.zhihu.DailyEntity;
import com.qihuan.find.net.Client;
import com.qihuan.find.presenter.base.BasePresenter;
import com.qihuan.find.presenter.base.PresenterEvent;
import com.qihuan.find.view.i.INewsView;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * NewsPresenter
 * Created by Qi on 2017/6/22.
 */

public class NewsPresenter extends BasePresenter<INewsView> {
    public void load() {
        Client.getZhihuApi()
                .getLatestDaily()
                .compose(this.<DailyEntity>bindUntilEvent(PresenterEvent.DETACHED))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DailyEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        getView().s();
                    }

                    @Override
                    public void onNext(@NonNull DailyEntity dailyEntity) {
                        getView().get(dailyEntity);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        getView().error(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
