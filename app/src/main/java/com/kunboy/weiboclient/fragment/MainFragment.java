package com.kunboy.weiboclient.fragment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kunboy.weiboclient.R;
import com.kunboy.weiboclient.account.AccessTokenKeeper;
import com.kunboy.weiboclient.adapter.HomelineAdapter;
import com.kunboy.weiboclient.animator.SlideInDownAnimator;
import com.kunboy.weiboclient.debug.DebugLog;
import com.kunboy.weiboclient.http.AbstractOkHttpCallBack;
import com.kunboy.weiboclient.http.HomeTimeLineImpl;
import com.kunboy.weiboclient.ui.CustomSwipeRefreshLayout;
import com.kunboy.weiboclient.ui.FloatingActionButton;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sunhongkun on 2015/2/8.
 */
public class MainFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    //OKHttp
    private OkHttpClient mOkHttpClient;
    private RecyclerView mRecyclerView;
    //自定义下拉刷新控件
    private CustomSwipeRefreshLayout mSwipeRefreshLayout;
    //RecyclerView的layoutManager
    private RecyclerView.LayoutManager mLayoutManager;
    //展示数据Recycler的Adapter
    private HomelineAdapter mAdapter;
    //虚拟数据源
    private ArrayList<Status> mDataset = new ArrayList<Status>();
    private int addedIndex = 0;
    //可隐藏title的标题显示
    private TextView mTitleBar;
    //位于Recyclerview上方的占位View
    private View mBlank;
    /**
     * 用于标记floatButton以及可伸缩的title是否已经显示出来，避免重复调用动画
     */
    private boolean isShown = true;
    //发表一条微博 按钮
    private FloatingActionButton editButton;
    //整体可伸缩title根布局
    private RelativeLayout mTitleBarLayout;

    /**
     * 每页请求的数量
     */
    public static final int PAGECOUNT = 50;
    /**
     * 当前请求道第几页
     */
    private int currentPage = 1;

    /** 当前的 Token */
    protected Oauth2AccessToken mAccessToken;

    private boolean isLoadingMore = false;
    public MainFragment() {

    }

    public static MainFragment getNewInstance() {
        MainFragment instance = new MainFragment();
        Bundle args = new Bundle();
        instance.setArguments(args);
        return instance;
    }

    ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOkHttpClient = new OkHttpClient();
        mAccessToken = AccessTokenKeeper.readAccessToken(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main,
                container, false);
        initViews(rootView);
        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListeners();
        mSwipeRefreshLayout.setRefreshing(true);
        getHomeTimeLine();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getHomeTimeLine() {

        final Request request = new HomeTimeLineImpl(mAccessToken,"0","0",PAGECOUNT,currentPage++,0,0,0).buildRequest();
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                mOkHttpClient.cancel(request.tag());
//            }
//        }.start();
//        mOkHttpClient.cancel(request.tag());
        mOkHttpClient.newCall(request).enqueue(new GetHomeTimelineCallback());

    }

    private void setListeners() {
        //设置下拉刷新的刷新监听器
        mSwipeRefreshLayout
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        titleBarAnimator(false);
                        swipViewAnimator(false);
                        editButton.show();
                        getHomeTimeLine();
//                        addOne();
                    }

                });
        //设置RecyclerView的滚动监听器，为了解决SwipeRefreshLayout和RecyclerView冲突的问题
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView,
                                             int newState) {
                // TODO Auto-generated method stub
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
                        int x = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                        int y = recyclerView.getAdapter().getItemCount();
                        if( x >= y-1){
                            Toast.makeText(getActivity(),"加载更多",Toast.LENGTH_SHORT).show();
                            if (!isLoadingMore){
                                isLoadingMore = true;
                                showMoreStatus();
                            }
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // TODO Auto-generated method stub
                super.onScrolled(recyclerView, dx, dy);
                int topRowVerticalPosition = (recyclerView == null || recyclerView
                        .getChildCount() == 0) ? 0 : recyclerView.getChildAt(0)
                        .getTop();
                mSwipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
            }

        });

        //设置自定义滚动监听器，用于监听是否需要隐藏和展示floatbutton以及title栏目
        mSwipeRefreshLayout.setCustomScrollListener(new CustomSwipeRefreshLayout.CustomScrollListener() {

            @Override
            public void onScroll(boolean scrollUp) {
                titleBarAnimator(scrollUp);
                swipViewAnimator(scrollUp);
                if (scrollUp) {
                    editButton.hide();
                } else {
                    editButton.show();
                }
            }
        });

        //设置onItemClickListener，这里需要注意下一，由于调用了部分更新的方法，所以position位置可能是错误的，所以没有将position作为参数传上来
        mAdapter.setOnItemClickListener(new HomelineAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view) {
                Toast.makeText(getActivity(), "点击：" + view.getTag(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMoreStatus() {
        getHomeTimeLine();
    }

    private void initViews(View rootView) {
        mSwipeRefreshLayout = (CustomSwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        mTitleBar = (TextView) rootView.findViewById(R.id.title);
        mBlank = rootView.findViewById(R.id.blank);
        editButton = (FloatingActionButton) rootView.findViewById(R.id.edit_button);
        mTitleBarLayout = (RelativeLayout) rootView.findViewById(R.id.title_bar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.status_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        //设置RecyclerView的默认动画，这里可以换成其他的动画效果
        mRecyclerView.setItemAnimator(new SlideInDownAnimator());
        mAdapter = new HomelineAdapter(getActivity(),mDataset);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * @param scrollUp 是否上滑
     */
    protected void titleBarAnimator(boolean scrollUp) {
        // TODO Auto-generated method stub

        if (scrollUp) {
            if (!isShown) return;
            ValueAnimator mAnimator = ValueAnimator.ofFloat(0, mTitleBarLayout.getHeight());
            mAnimator.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isShown = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }
            });
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Float animationValue = (Float) animation.getAnimatedValue();
                    mTitleBarLayout.setTranslationY(-animationValue);
                }
            });
            mAnimator.setDuration(500);
            mAnimator.setTarget(mTitleBarLayout);
            mAnimator.start();
        } else {
            if (isShown) return;
            ValueAnimator mAnimator = ValueAnimator.ofFloat(mTitleBarLayout.getHeight(), 0);
            mAnimator.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isShown = true;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }
            });
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Float animationValue = (Float) animation.getAnimatedValue();
                    mTitleBarLayout.setTranslationY(-animationValue);
                }
            });
            mAnimator.setDuration(500);
            mAnimator.setTarget(mTitleBarLayout);
            mAnimator.start();
        }

    }

    /**
     * @param scrollUp 是否上滑
     */
    protected void swipViewAnimator(boolean scrollUp) {
        // TODO Auto-generated method stub

        if (scrollUp) {
            if (!isShown) return;
            final RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mBlank.getLayoutParams();
            ValueAnimator mAnimator = ValueAnimator.ofInt(mTitleBarLayout.getHeight(), 0);
            mAnimator.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isShown = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }
            });
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animationValue = (int) animation.getAnimatedValue();
                    lp.height = animationValue;
                    mBlank.setLayoutParams(lp);
                }
            });
            mAnimator.setDuration(500);
            mAnimator.setTarget(mBlank);
            mAnimator.start();
        } else {
            if (isShown) return;
            final RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mBlank.getLayoutParams();
            ValueAnimator mAnimator = ValueAnimator.ofInt(0, mTitleBarLayout.getHeight());
            mAnimator.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isShown = true;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }
            });
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    // TODO Auto-generated method stub
                    int animationValue = (int) animation.getAnimatedValue();
                    lp.height = animationValue;
                    mBlank.setLayoutParams(lp);
                }
            });
            mAnimator.setDuration(500);
            mAnimator.setTarget(mBlank);
            mAnimator.start();
        }

    }


    class GetHomeTimelineCallback extends AbstractOkHttpCallBack {

        @Override
        public Object parse(Response response) {
            Headers responseHeaders = response.headers();
            if(DebugLog.isDebug()){
                for (int i = 0; i < responseHeaders.size(); i++) {
                    DebugLog.i(TAG, "parse", responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }
            }
            String jsonString = null;
            StatusList statusList;
            try {
                jsonString = response.body().string();
                DebugLog.i(TAG, "parse", jsonString);
                statusList = StatusList.parse(jsonString);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return statusList;
        }

        @Override
        public void doFailure(Request request, IOException ioexception) {
            onGetHomeTimelineFailure();
            mSwipeRefreshLayout.setRefreshing(false);
            isLoadingMore = false;
        }

        @Override
        public void doResponse(Object object) {
            int count = mDataset.size();
            int addCount = ((StatusList)object).statusList.size();
            if(addCount > 0){
                mDataset.addAll(((StatusList)object).statusList);
                mAdapter.setData(mDataset);
                mAdapter.notifyItemRangeInserted(count,addCount);
                onGetHomeTimelineSuccess();
            }
            mSwipeRefreshLayout.setRefreshing(false);
            isLoadingMore = false;
        }
    }


    private void onGetHomeTimelineSuccess() {
//        mAdapter.setData(mDataset);
//        mAdapter.notifyDataSetChanged();
    }

    private void onGetHomeTimelineFailure() {

    }
}
