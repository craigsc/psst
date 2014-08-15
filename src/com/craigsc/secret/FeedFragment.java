package com.craigsc.secret;

import java.util.List;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.craigsc.secret.FeedScrollListener.FeedPaginationListener;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.parse.ParseQueryAdapter.OnQueryLoadListener;

public class FeedFragment extends ListFragment 
    implements OnRefreshListener, FeedPaginationListener {
  private PullToRefreshLayout mPullToRefreshLayout;
  private FeedScrollListener mFeedScrollListener;
  private FeedAdapter mFeedAdapter;
  private AnimationAdapter mAnimationAdapter;
  
  @Override  
  public View onCreateView(
    LayoutInflater inflater, 
    ViewGroup container,  
    Bundle savedInstanceState
  ) {
    mFeedAdapter = new FeedAdapter(inflater.getContext());
    mFeedAdapter.addOnQueryLoadListener(new OnQueryLoadListener<Post>() {
      @Override
      public void onLoaded(List<Post> posts, Exception e) {
        if (e == null) {
          mPullToRefreshLayout.setRefreshComplete();
          mFeedScrollListener.informDoneLoading();
        } else {
          // TODO error handling
        }
      }
      @Override
      public void onLoading() { }
    });
    mAnimationAdapter = new AlphaInAnimationAdapter(mFeedAdapter);
    setListAdapter(mAnimationAdapter);
    return inflater.inflate(R.layout.fragment_feed, container, false);
  }
  
  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    mAnimationAdapter.setAbsListView(getListView());
    ViewGroup viewGroup = (ViewGroup) view;
    mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());
    ActionBarPullToRefresh.from(getActivity())
      .insertLayoutInto(viewGroup)
      .theseChildrenArePullable(
          getListView().getId(), 
          getListView().getEmptyView().getId()
      ).listener(this)
      .setup(mPullToRefreshLayout); 
    mFeedScrollListener = new FeedScrollListener(this);
    getListView().setOnScrollListener(mFeedScrollListener);
  }

  public FeedFragment reloadFeed() {
    mPullToRefreshLayout.setRefreshing(true);
    mFeedAdapter.loadObjects();
    return this;
  }
  
  public FeedFragment scrollToTop() {
    getListView().setSelectionAfterHeaderView();
    return this;
  }
  
  @Override
  public void onRefreshStarted(View view) {
    reloadFeed();
  }

  @Override
  public void loadNextPage() {
    mFeedAdapter.loadNextPage();
  }
 
}
