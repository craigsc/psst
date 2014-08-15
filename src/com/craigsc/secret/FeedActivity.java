package com.craigsc.secret;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;

public class FeedActivity extends BaseActivity {
  public final static int CREATE_POST_REQUEST = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_feed);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_write_post: 
        startActivityForResult(
            new Intent(this, NewPostActivity.class),
            CREATE_POST_REQUEST);
        break;
      case R.id.action_find_friends:
        // TODO find friends action
        break;
      case R.id.action_log_out:
        ParseUser.logOut();
        redirectIfLoggedOut();
        break;
    }
    return super.onOptionsItemSelected(item);
  }
  
  @Override
  protected void onActivityResult(
    int requestCode, 
    int resultCode, 
    Intent data) {
    if (requestCode == CREATE_POST_REQUEST && resultCode == RESULT_OK) {
      ((FeedFragment) getSupportFragmentManager()
          .findFragmentById(R.id.feed))
          .reloadFeed()
          .scrollToTop();
    }
  }
}
