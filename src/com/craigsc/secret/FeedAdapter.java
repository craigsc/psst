package com.craigsc.secret;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class FeedAdapter extends ParseQueryAdapter<Post> {
  
  public FeedAdapter(Context context) {
    super(context, new ParseQueryAdapter.QueryFactory<Post>() {
      @Override
      public ParseQuery<Post> create() {
        return new ParseQuery<Post>("Post").orderByDescending("createdAt");
      }
    });
  }
  
  @Override
  public View getNextPageView(View v, ViewGroup parent) {
    if (v == null) {
      v = View.inflate(getContext(), R.layout.loading_spinner, null);
    }
    return v;
  }
  
  @Override
  public View getItemView(final Post post, View v, ViewGroup parent) {
    if (v == null) {
      v = View.inflate(getContext(), R.layout.item_post, null);
    }
    super.getItemView(post, v, parent);

    TextView postText = (TextView) v.findViewById(R.id.post_text);
    final ImageView iv = (ImageView) v.findViewById(R.id.like);
    final TextView likeCount = (TextView) v.findViewById(R.id.like_count);
    postText.setText(post.getText());
    if (post.getColor() == v.getResources().getColor(R.color.white)) {
      postText.setTextColor(Color.BLACK); 
      likeCount.setTextColor(v.getResources().getColor(R.color.gray));
    } else {
      postText.setTextColor(Color.WHITE);
      likeCount.setTextColor(Color.WHITE);
    }
    postText.setBackgroundColor(post.getColor());
    
    if (post.getIsLikedByViewer()) {
      iv.setImageDrawable(v.getResources().getDrawable(R.drawable.heart_red));
    } else {
      iv.setImageDrawable(getHeartDrawableForPost(post, v.getResources()));
    }    
    updateLikeCount(post, likeCount);
    
    iv.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!post.getIsLikedByViewer()) {
          post.setIsLikedByViewer(true);
          iv.setImageDrawable(
              v.getResources().getDrawable(R.drawable.heart_red));
        } else {
          post.setIsLikedByViewer(false);
          iv.setImageDrawable(getHeartDrawableForPost(post, v.getResources()));
        }
        updateLikeCount(post, likeCount);
        post.saveInBackground();
      } 
    });
    return v;
  }
  
  private void updateLikeCount(Post post, TextView likeCount) {
    int numLikes = post.getLikeCount();
    likeCount.setText(numLikes > 0 ? Integer.toString(numLikes) : "");
  }

  private Drawable getHeartDrawableForPost(Post post, Resources resources) {
    if (post.getColor() == resources.getColor(R.color.white)) {
      return resources.getDrawable(R.drawable.heart_gray);
    }
    return resources.getDrawable(R.drawable.heart_white);
  }
}
