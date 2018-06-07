package com.qihuan.dailymodule.view

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.bingoogolapple.bgabanner.BGABanner
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.qihuan.commonmodule.base.BaseMvpFragment
import com.qihuan.commonmodule.bus.BindEventBus
import com.qihuan.commonmodule.bus.event.RefreshEvent
import com.qihuan.commonmodule.router.Routes
import com.qihuan.commonmodule.utils.inflate
import com.qihuan.dailymodule.R
import com.qihuan.dailymodule.contract.DailyContract
import com.qihuan.dailymodule.model.bean.DailyItemBean
import com.qihuan.dailymodule.model.bean.TopStoryBean
import com.qihuan.dailymodule.presenter.DailyPresenter
import com.qihuan.dailymodule.view.adapter.BannerAdapter
import com.qihuan.dailymodule.view.adapter.DailyAdapter
import kotlinx.android.synthetic.main.fragment_news.*
import org.greenrobot.eventbus.Subscribe

/**
 * DailyFragment
 *
 * @author Qi
 */
@BindEventBus
@Route(path = Routes.DAILY_FRAGMENT)
class DailyFragment : BaseMvpFragment<DailyContract.View, DailyContract.Presenter>(), DailyContract.View, BaseQuickAdapter.OnItemClickListener, BGABanner.Delegate<View, TopStoryBean> {

    private var bannerView: BGABanner? = null
    private var dailyAdapter: DailyAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // list
        linearLayoutManager = LinearLayoutManager(context)
        rv_list.layoutManager = linearLayoutManager

        // banner
        bannerView = context?.inflate(R.layout.layout_banner, rv_list)
        bannerView?.setAdapter(BannerAdapter())
        bannerView?.setDelegate(this)

        // adapter
        dailyAdapter = DailyAdapter()
        dailyAdapter?.apply {
            openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM)
            addHeaderView(bannerView)
        }?.onItemClickListener = this
        rv_list.adapter = dailyAdapter

        // refresh layout
        refresh_layout.apply {
            setOnRefreshListener { mPresenter.getLatestDaily() }
            setOnLoadMoreListener { mPresenter.getBeforeDaily() }
            autoRefresh()
        }
    }

    override fun onResume() {
        super.onResume()
        bannerView?.startAutoPlay()
    }

    override fun onPause() {
        super.onPause()
        bannerView?.stopAutoPlay()
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val dailyItemBean = adapter.data[position] as DailyItemBean
        if (dailyItemBean.isHeader) {
            return
        }
        if (dailyItemBean.t == null) {
            return
        }
        ARouter.getInstance()
                .build(Routes.DAILY_DET_ACTIVITY)
                .withInt("id", dailyItemBean.t.id)
                .navigation()
    }

    override fun onBannerItemClick(banner: BGABanner, itemView: View, model: TopStoryBean?, position: Int) {
        model?.run {
            ARouter.getInstance()
                    .build(Routes.DAILY_DET_ACTIVITY)
                    .withInt("id", id)
                    .navigation()
        }
    }

    override fun latestDaily(topList: List<TopStoryBean>) {
        bannerView?.setData(R.layout.item_daily_banner, topList, null)
    }

    override fun beforeDaily(isRefresh: Boolean, dailyList: List<DailyItemBean>) {
        if (isRefresh) {
            dailyAdapter?.setNewData(dailyList)
        } else {
            dailyAdapter?.addData(dailyList)
        }
    }

    override fun onRefreshEnd(success: Boolean) {
        refresh_layout.finishRefresh(success)
    }

    override fun onLoadMoreEnd(success: Boolean) {
        refresh_layout.finishLoadMore(success)
    }

    @Subscribe
    fun onRefreshEvent(refreshEvent: RefreshEvent) {
        linearLayoutManager?.run {
            val visibleItemPosition = findFirstCompletelyVisibleItemPosition()
            if (visibleItemPosition == 0) {
                // 刷新
                refresh_layout.autoRefresh()
                return
            }
            // 超过 20 条, 先滚动到20条, 再平滑滚动
            if (visibleItemPosition > 20) {
                rv_list.scrollToPosition(20)
            }
            rv_list.smoothScrollToPosition(0)
        }
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showError(errorMsg: String) {

    }

    override fun initPresenter(): DailyContract.Presenter {
        return DailyPresenter()
    }

    companion object {

        fun newInstance(): DailyFragment {
            return DailyFragment()
        }
    }
}