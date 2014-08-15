package com.craigsc.secret;

import java.util.Random;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class NewPostFragment extends Fragment {
  private static final int MAX_LINES = 10;
  private static final int[] COLORS = new int[] {
    R.color.black,
    R.color.turquoise,
    R.color.violet,
    R.color.algae,
    R.color.light_orange,
    R.color.light_red,
    R.color.white,
    R.color.blue,
    R.color.purple,
    R.color.green,
    R.color.orange,
    R.color.red
  };
  
  private OnTextEnteredListener mTextListener;
  private int mCurrentColor = new Random().nextInt(COLORS.length);
  private EditText mTextInput;
  private GestureDetector mGestureDetector;
  private SwipeDetector mSwipeDetector;
  
  public interface OnTextEnteredListener {
    public void setIsTextEntered(boolean isTextEntered);
  }
  
  @Override
  public View onCreateView(
    LayoutInflater inflater, 
    ViewGroup parent, 
    Bundle savedInstanceState
  ) {
    View v = inflater.inflate(R.layout.fragment_new_post, parent, false);
    mTextInput = (EditText) v.findViewById(R.id.post_text);
    mTextInput.setHorizontallyScrolling(false);
    mTextInput.setLines(MAX_LINES);
    
    // focus listener to handle cursor and placeholder text
    mTextInput.setOnFocusChangeListener(new OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          mTextInput.setHint("");
        } else {
          mTextInput.setHint(R.string.post_placeholder);
        }
        mTextInput.setCursorVisible(hasFocus);
      }
    });
    
    // Setup background colors and swipe listener
    changeTextInputColor(0);
    mSwipeDetector = new SwipeDetector();
    mGestureDetector = new GestureDetector(getActivity(), mSwipeDetector);
    mTextInput.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
      } 
    });
    
    // When done button is pressed, clear focus
    mTextInput.setOnEditorActionListener(new OnEditorActionListener() {        
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
          dismissSoftKeyboard();
        }
        return false;
      }
    });
    
    // Disable newlines, clear focus on back
    mTextInput.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_ENTER;
      }
    });
    
    // Text Listener
    mTextInput.addTextChangedListener(new TextWatcher() {
      @Override
      public void afterTextChanged(Editable s) {
        mTextListener.setIsTextEntered(s.toString().trim().length() > 0);
      }

      @Override
      public void beforeTextChanged(
        CharSequence s, 
        int start, 
        int count, 
        int after
      ) {}

      @Override
      public void onTextChanged(
        CharSequence s, 
        int start, 
        int before, 
        int count
      ) {}  
    });
    return v;
  }
  
  private void changeTextInputColor(int diff) {
    mCurrentColor = (mCurrentColor + diff) % COLORS.length;
    if (mCurrentColor < 0) {
      mCurrentColor += COLORS.length;
    }
    mTextInput.setBackgroundColor(
      getResources().getColor(COLORS[mCurrentColor])
    );
    
    if (COLORS[mCurrentColor] == R.color.white) {
      mTextInput.setTextColor(Color.BLACK);
      mTextInput.setHintTextColor(Color.BLACK);
    } else {
      mTextInput.setTextColor(Color.WHITE);
      mTextInput.setHintTextColor(Color.WHITE);
    }
  }
  
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mTextListener = (OnTextEnteredListener) activity;
  }
  
  public void savePost() {
    dismissSoftKeyboard();
    final Dialog progressDialog = new ProgressDialog(getActivity());
    progressDialog.setCancelable(false);
    progressDialog.show();
    progressDialog.setContentView(R.layout.loading_spinner);
    
    new Post()
      .setAuthor(ParseUser.getCurrentUser())
      .setText(mTextInput.getText().toString())
      .setColor(getResources().getColor(COLORS[mCurrentColor]))
      .saveInBackground(new SaveCallback() {
        @Override
        public void done(ParseException e) {
          if (e == null) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
          } else {
            Toast.makeText(
              getActivity(), 
              "Error saving: " + e.getMessage(), 
              Toast.LENGTH_SHORT).show();
          }
          progressDialog.dismiss();
        }
      });
  }
  
  private class SwipeDetector extends SimpleOnGestureListener {
    private static final int MIN_VELOCITY = 200;
    private static final int MIN_HORIZONTAL = 120;
    private static final int MAX_VERTICAL = 250;
    
    private boolean swipeDetected = false;
    
    @Override
    public boolean onFling(
      MotionEvent e1, 
      MotionEvent e2, 
      float velocityX, 
      float velocityY
    ) {
      if (Math.abs(velocityX) < MIN_VELOCITY || 
          Math.abs(e1.getY() - e2.getY()) > MAX_VERTICAL) {
        return false;
      }
      // right to left swipe
      if (e1.getX() - e2.getX() > MIN_HORIZONTAL) {
        changeTextInputColor(-1);
        swipeDetected = true;
      } else if (e2.getX() - e1.getX() > MIN_HORIZONTAL) {
        changeTextInputColor(1);
        swipeDetected = true;
      }
      return false;
    }
    
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
      if (!swipeDetected) {
        mTextInput.requestFocus();
      }
      return false;
    }
    
    @Override
    public boolean onDown(MotionEvent e) {
      swipeDetected = false;
      return true;
    }
  }
  
  public void dismissSoftKeyboard() {
    InputMethodManager imm = (InputMethodManager) getActivity()
        .getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(mTextInput.getWindowToken(), 0);
    mTextInput.clearFocus();
  }
}
