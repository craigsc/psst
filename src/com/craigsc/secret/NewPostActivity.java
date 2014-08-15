package com.craigsc.secret;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.craigsc.secret.NewPostFragment.OnTextEnteredListener;

public class NewPostActivity extends BaseActivity 
  implements OnTextEnteredListener {
  
  private boolean mIsTextEntered = false;
  
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_post);
    setupActionBar();
    
    FragmentManager manager = getSupportFragmentManager();
    Fragment fragment = manager.findFragmentById(R.id.newPostFragment);

    if (fragment == null) {
      fragment = new NewPostFragment();
      manager.beginTransaction().add(R.id.newPostFragment, fragment).commit();
    }
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case android.R.id.home:
      ((NewPostFragment) getSupportFragmentManager()
          .findFragmentById(R.id.newPostFragment))
          .dismissSoftKeyboard();
      setResult(RESULT_CANCELED);
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  private void setupActionBar() {
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowTitleEnabled(false); 
    getSupportActionBar().setDisplayShowCustomEnabled(true);
    getSupportActionBar().setCustomView(R.layout.action_bar_new_post);
    View postButton = getSupportActionBar().getCustomView()
        .findViewById(R.id.action_post);
    postButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          ((NewPostFragment) getSupportFragmentManager()
            .findFragmentById(R.id.newPostFragment))
            .savePost();
        }
    });
    postButton.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          ((TextView)findViewById(R.id.action_post)).setTextColor(Color.GRAY);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
          ((TextView)findViewById(R.id.action_post)).setTextColor(Color.BLACK);
        }
        return false;
      }
    });
  }
  
  @Override
  public void setIsTextEntered(boolean isTextEntered) {
    if (isTextEntered != mIsTextEntered) {
      mIsTextEntered = isTextEntered;
      getSupportActionBar().getCustomView().findViewById(R.id.action_post)
        .setVisibility(isTextEntered ? View.VISIBLE : View.INVISIBLE);
    }
  }
}
