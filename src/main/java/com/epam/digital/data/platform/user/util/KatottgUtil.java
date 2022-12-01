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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class KatottgUtil {

  private KatottgUtil() {
  }

  // UA03 02 001 001 01 12345 - divided into groups

  private static final String COUNTRY_PATTERN = "^UA$";                            // UA
  private static final String REGION_PATTERN = "^UA\\d{2}0{10}\\d{5}$";            // UA03 000000000012345
  private static final String AREA_PATTERN = "^UA\\d{4}0{8}\\d{5}$";               // UA0302 0000000012345
  private static final String TERRITORIAL_BULK_PATTERN = "^UA\\d{7}0{5}\\d{5}$";   // UA0302001 0000012345
  private static final String LOCALITY_PATTERN = "^UA\\d{10}0{2}\\d{5}$";          // UA0302001001 0012345
//  private static final String DISTRICT_PATTERN = "^UA\\d{12}\\d{5}$";            // UA030200100101 12345

  private static final int COUNTRY_LENGTH = 2;
  private static final int REGION_LENGTH = 4;
  private static final int AREA_LENGTH = 6;
  private static final int TERRITORIAL_BULK_LENGTH = 9;
  private static final int LOCALITY_LENGTH = 12;
  private static final int DISTRICT_LENGTH = 14;

  public static List<String> retainPrefixes(List<String> katottgs) {
    var sortedPrefixes = katottgs.stream()
        .map(KatottgUtil::cut)
        .distinct()
        .sorted(Comparator.comparing(String::length))
        .collect(Collectors.toList());
    return collapse(sortedPrefixes);
  }

  private static String cut(String katottg) {
    int length = DISTRICT_LENGTH;
    if (katottg.matches(COUNTRY_PATTERN)) {
      length = COUNTRY_LENGTH;
    } else if (katottg.matches(REGION_PATTERN)) {
      length = REGION_LENGTH;
    } else if (katottg.matches(AREA_PATTERN)) {
      length = AREA_LENGTH;
    } else if (katottg.matches(TERRITORIAL_BULK_PATTERN)) {
      length = TERRITORIAL_BULK_LENGTH;
    } else if (katottg.matches(LOCALITY_PATTERN)) {
      length = LOCALITY_LENGTH;
    }
    return katottg.substring(0, length);
  }

  private static List<String> collapse(List<String> sortedPrefixes) {
    List<String> buffer = new ArrayList<>();
    for (int i = 0; i < sortedPrefixes.size() - 1; i++) {
      for (int j = i + 1; j < sortedPrefixes.size(); j++) {
        if (sortedPrefixes.get(j).startsWith(sortedPrefixes.get(i))) {
          buffer.add(sortedPrefixes.get(j));
        }
      }
    }
    sortedPrefixes.removeAll(buffer);
    return sortedPrefixes;
  }
}

