package com.craigsc.secret;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;

import com.parse.ParseUser;

public class BaseActivity extends ActionBarActivity {
  
  protected void redirectIfLoggedOut() {
    if (ParseUser.getCurrentUser() == null) {
      startActivity(new Intent(this, LoginActivity.class));
      finish();
    }
  }
  
  @Override
  protected void onResume() {
    //ParseUser.logOut();
    redirectIfLoggedOut();
    super.onResume();
  }

  @Override
  protected void onRestart() {
    redirectIfLoggedOut();
    super.onRestart();
  }
}
