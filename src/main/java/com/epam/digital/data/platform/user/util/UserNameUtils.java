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

public class UserNameUtils {

  private UserNameUtils() {
  }

  public static  String getFirstAndMiddleName(String fullName) {
    if (fullName != null && fullName.contains(" ") && fullName.indexOf(" ") + 1 <= fullName
        .length()) {
      return fullName.substring(fullName.indexOf(" ") + 1);
    }
    return "";
  }

  public static String getLastName(String fullName) {
    if (fullName != null && fullName.contains(" ")) {
      return fullName.substring(0, fullName.indexOf(" "));
    }
    return "";
  }
}
