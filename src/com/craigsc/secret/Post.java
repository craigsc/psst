package com.craigsc.secret;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {
  public Post() {}
  
  public String getText() {
    return getString("text");
  }
  
  public Post setText(String text) {
    put("text", text);
    return this;
  }
  
  public ParseUser getAuthor() {
    return getParseUser("author");
  }
  
  public Post setAuthor(ParseUser user) {
    put("author", user);
    return this;
  }
  
  public Post setColor(int color) {
    put("color", color);
    return this;
  }
  
  public int getColor() {
    return getInt("color");
  }
  
  public int getLikeCount() {
    List<String> likers = getList("likers");
    return likers == null ? 0 : likers.size();
  }
  
  public boolean getIsLikedByViewer() {
    List<String> likers = getList("likers");
    if (likers == null || likers.isEmpty()) {
      return false;
    }
    return likers.contains(ParseUser.getCurrentUser().getObjectId());
  }
  
  public Post setIsLikedByViewer(boolean shouldLike) {
    List<String> likerIds = getList("likers");
    if (likerIds == null) {
      likerIds = new ArrayList<String>();
    }
    String viewerId = ParseUser.getCurrentUser().getObjectId();
    boolean isLiked = getIsLikedByViewer();
    if (!isLiked && shouldLike) {
      likerIds.add(viewerId);
    } else if (isLiked && !shouldLike) {
      likerIds.remove(viewerId);
    }
    put("likers", likerIds);
    return this;
  }  
}
