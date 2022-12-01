/*
 * Copyright 2022 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
