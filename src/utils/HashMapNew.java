package utils;

import java.util.HashMap;

public class HashMapNew extends HashMap<String, String> implements Cloneable{
  static final long serialVersionUID = 1L;

  public String get(Object key){
    String value = (String)super.get(key);
    if (value == null) {
      return "";
    }
    return value;
  }

  public String put(String key, String value) {
    String val = super.put(key, value);
    return val;
  }

  @Override
  public Object clone() {
    HashMapNew cloned = (HashMapNew)super.clone();
    cloned.putAll(this);
    return cloned;
  }
}
