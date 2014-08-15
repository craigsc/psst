package com.craigsc.secret;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.craigsc.secret.LoginFragment.LoginListener;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class LoginActivity extends FragmentActivity implements LoginListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ParseObject.registerSubclass(Post.class);
    ParseObject.registerSubclass(Friendship.class);
    Parse.initialize(
      this, 
      getString(R.string.parse_app_id), 
      getString(R.string.parse_client_key));
    ParseFacebookUtils.initialize(getString(R.string.fb_app_id));
    redirectIfLoggedIn();
    setContentView(R.layout.activity_login);
    
    FragmentManager manager = getSupportFragmentManager();
    Fragment fragment = manager.findFragmentById(R.id.loginFragment);

    if (fragment == null) {
      fragment = new LoginFragment();
      manager.beginTransaction().add(R.id.loginFragment, fragment).commit();
    }
  }
  
  private void redirectIfLoggedIn() {
    if (ParseUser.getCurrentUser() != null) {
      startActivity(new Intent(this, FeedActivity.class)
          .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
      finish();
    }
  }
  
  @Override
  protected void onResume() {
    redirectIfLoggedIn();
    super.onResume();
  }

  @Override
  protected void onRestart() {
    redirectIfLoggedIn();
    super.onRestart();
  }
  
  @Override
  public void onBackPressed() {
    startActivity(new Intent(Intent.ACTION_MAIN)
        .addCategory(Intent.CATEGORY_HOME)
        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
  }
  
  @Override
  protected void onActivityResult(
      int requestCode, 
      int resultCode, 
      Intent data) {
    ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
  }

  @Override
  public void loggedIn() {
    redirectIfLoggedIn();
  }
}
