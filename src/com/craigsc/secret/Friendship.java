package com.craigsc.secret;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Friendship")
public class Friendship extends ParseObject {
  public Friendship() {}
  
  public Friendship setRawFromId(String id) {
    return setEncodedFromId(HashUtil.encode(id));
  }
  
  public Friendship setEncodedFromId(String id) {
    put("from", id);
    return this;
  }
  
  public String getFromId() {
    return getString("from");
  }
  
  public Friendship setRawToId(String id) {
    return setEncodedToId(HashUtil.encode(id));
  }
  
  public Friendship setEncodedToId(String id) {
    put("to", id);
    return this;
  }
  
  public String getToId() {
    return getString("to");
  }
  
}
