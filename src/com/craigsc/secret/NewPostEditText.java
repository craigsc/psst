package com.craigsc.secret;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class NewPostEditText extends EditText {

  public NewPostEditText(Context context) {
    super(context);
  }

  public NewPostEditText(Context context, AttributeSet attributes) {
    super(context, attributes);
  }
  
  @Override
  public boolean onKeyPreIme(int keyCode, KeyEvent event) {
    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
      clearFocus();
    }
    return super.dispatchKeyEvent(event);
  }
}
