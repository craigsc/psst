package com.craigsc.secret;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphObject;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class LoginFragment extends Fragment {
  private static final String FB_INFO_QUERY = "{" + 
      "'networks': 'SELECT affiliations FROM user WHERE uid=me()'," +
      "'friends': 'SELECT uid1, uid2 FROM friend WHERE uid1=me()'," +
      "}";
  private LoginListener mLoginListener;
  private Dialog mProgressDialog;
  
  public interface LoginListener {
    public void loggedIn();
  }
  
  @Override
  public View onCreateView(
      LayoutInflater inflater,
      ViewGroup parent,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_login, parent, false);
    v.findViewById(R.id.appName).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        facebookLogin();
      }    
    });
    return v;
  }
  
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mLoginListener = (LoginListener) activity;
  }
  
  private void facebookLogin() {
    mProgressDialog = new ProgressDialog(getActivity());
    mProgressDialog.setCancelable(false);
    mProgressDialog.show();
    mProgressDialog.setContentView(R.layout.loading_spinner);
    
    ParseFacebookUtils.logIn(getActivity(), new LogInCallback() {
      @Override
      public void done(ParseUser user, ParseException e) {
        if (e != null) {
          Toast.makeText(
              getActivity(), 
              "Login error: " + e.getMessage(), 
              Toast.LENGTH_SHORT).show();
        }
        if (user == null) {
          mProgressDialog.dismiss();
          return;
        }
        
        makeNetworkRequest(user.isNew());
      }
    });
  }
  
  private void makeNetworkRequest(final boolean blocking) {
    Bundle params = new Bundle();
    params.putString("q", FB_INFO_QUERY);
    new Request(
        ParseFacebookUtils.getSession(), 
        "/fql", 
        params,
        HttpMethod.GET,
        new Request.Callback() {  
          @Override
          public void onCompleted(Response response) {
            if ((response.getError() != null || 
                !facebookCallback(response.getGraphObject(), blocking)) &&
                blocking) {
              loginErrorOccurredBlocking();
            }
          }
        }
    ).executeAsync();
    if (!blocking) {
      mLoginListener.loggedIn();
      mProgressDialog.dismiss();
    }
  }
  
  private void loginErrorOccurredBlocking() {
    Toast.makeText(
        getActivity(), 
        "Unexpected login error occurred!", 
        Toast.LENGTH_SHORT).show();
    mProgressDialog.dismiss();
  }
  
  private boolean facebookCallback(GraphObject graphObject, boolean blocking) {
    if (graphObject == null) {
      return false;
    }
    
    try {
      JSONArray data = graphObject.getInnerJSONObject().getJSONArray("data");
      JSONObject response;
      String queryName;
      for (int i = 0; i < data.length(); i++) {
        response = data.getJSONObject(i);
        queryName = response.getString("name");
        if (queryName.equals("networks")) {
          saveNetworkInformation(response.getJSONArray("fql_result_set"));
        } else if (queryName.equals("friends")) {
          saveFriendInformation(
              response.getJSONArray("fql_result_set"), 
              blocking);
        }
      }      
      return true;
    } catch (JSONException e) {
      return false;
    }
  }
  
  /**
   * Data format is [{"affiliations":[
   *  {"type":"work","nid":"50431648","name":"Facebook"},
   *  {"type":"high school","nid":"33568224","name":"Milton High School"},
   *  ...
   * ]}]
   * @param data
   * @throws JSONException
   */
  private void saveNetworkInformation(JSONArray data) throws JSONException {
    JSONArray networks = data.getJSONObject(0).getJSONArray("affiliations");
    ParseUser.getCurrentUser().put("networks", networks);
    ParseUser.getCurrentUser().saveInBackground();
  }
  
  /**
   * Data format is [{"uid2":"4"},{"uid2":"1749"},...]
   * @param data
   * @throws JSONException
   */
  private void saveFriendInformation(
      final JSONArray data, 
      final boolean blocking) throws JSONException {
    List<ParseObject> friendships = new ArrayList<ParseObject>();
    JSONObject friendship;
    for (int i = 0; i < data.length(); i++) {
      friendship = data.getJSONObject(i);
      friendships.add(new Friendship()
        .setRawFromId(friendship.getString("uid1"))
        .setRawToId(friendship.getString("uid2")));
    }
    Friendship.saveAllInBackground(friendships, new SaveCallback() {
      @Override
      public void done(ParseException e) {
        if (!blocking) {
          return;
        }

        if (e != null) {
          Log.d("CRAIG", e.getMessage());
          loginErrorOccurredBlocking();
        } else {
          mLoginListener.loggedIn();
          mProgressDialog.dismiss();
        }
      }   
    });
  }
  
  /*
  private void loginAnonymously() {
    ParseAnonymousUtils.logIn(new LogInCallback() {
      @Override
      public void done(ParseUser user, ParseException e) {
        if (e == null) {
          mLoginListener.loggedIn();
        }
      }
    });
  }*/
}
