package com.craigsc.secret;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

class FeedScrollListener implements OnScrollListener {
  private static final int VISIBLE_THRESHOLD = 3;
  
  private boolean loading = true;
  private FeedPaginationListener mFeedCallback;
  
  public interface FeedPaginationListener {
    public void loadNextPage();
  }
  
  public FeedScrollListener(FeedPaginationListener callback) {
    mFeedCallback = callback;
  }
  
  @Override
  public void onScroll(AbsListView view, int firstVisibleItem,
      int visibleItemCount, int totalItemCount) {
    if (loading) {
      return;
    }

    if ((totalItemCount - visibleItemCount) <= 
        (firstVisibleItem + VISIBLE_THRESHOLD)) {
      loading = true;
      mFeedCallback.loadNextPage();   
    }
  }
  
  public void informDoneLoading() {
    this.loading = false;
  }
  
  @Override
  public void onScrollStateChanged(AbsListView view, int scrollState) { }
  
}
