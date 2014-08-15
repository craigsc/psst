package com.craigsc.secret;

import java.util.HashMap;
import java.util.Map;

import com.parse.codec.binary.Hex;
import com.parse.codec.digest.DigestUtils;

public class HashUtil {
  private final static String SALT = "ldksjfd82711jk111kjh";
  private static Map<String, String> cache = new HashMap<String, String>();
  
  public static String encode(String raw) {
    if (!cache.containsKey(raw)) {
      cache.put(raw, Hex.encodeHexString(DigestUtils.sha(SALT + raw)));
    }
    return cache.get(raw);
  }
}
