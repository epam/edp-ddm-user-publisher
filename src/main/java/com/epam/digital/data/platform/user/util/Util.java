package com.epam.digital.data.platform.user.util;

import java.util.List;
import java.util.Map;

public class Util {

  private Util() {
  }

  public static String listToString(List<String> list) {
    return list == null || list.isEmpty() ? null : String.join(",", list);
  }

  public static List<String> trimToNull(List<String> list) {
    removeEmptyElements(list);
    return list == null || list.isEmpty() ? null : list;
  }

  public static Map<String, List<String>> trimToNull(Map<String, List<String>> map) {
    removeEmptyElements(map);
    return map == null || map.isEmpty() ? null : map;
  }

  public static List<String> removeEmptyElements(List<String> list) {
    if (list != null) {
      list.removeIf(String::isBlank);
    }
    return list;
  }

  public static void removeEmptyElements(Map<String, List<String>> map) {
    if (map != null) {
      map.values()
          .removeIf(list -> list == null || list.isEmpty() || removeEmptyElements(list).isEmpty());
    }
  }
}
