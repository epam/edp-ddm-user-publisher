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

package com.epam.digital.data.platform.user.skip;

import static com.epam.digital.data.platform.user.util.Constants.FIRST_USER_OFFSET;

import com.epam.digital.data.platform.user.model.SkippingResult;
import com.epam.digital.data.platform.user.model.User;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DuplicatesSkipper extends Skipper {

  @Override
  public SkippingResult check(List<User> users, SkippingResult results) {
    var duplicates = getDuplicates(users);
    for (var entry : duplicates.entrySet()) {
      results.add(entry.getKey(), createMessage(entry));
    }
    return checkNext(users, results);
  }

  private String createMessage(Entry<Integer, Integer> entry) {
    return String.format("The user will be skipped because it's duplicate of user in row '%s'",
        entry.getValue() + FIRST_USER_OFFSET);
  }

  private Map<Integer, Integer> getDuplicates(List<User> users) {
    Map<Integer, Integer> result = new HashMap<>();
    if (users.size() < 2 || new HashSet<>(users).size() == users.size()) {
      return result;
    }
    for (int duplicate = 1; duplicate < users.size(); duplicate++) {
      for (int main = 0; main < duplicate; main++) {
        if (users.get(duplicate).equals(users.get(main))) {
          result.put(duplicate, main);
          break;
        }
      }
    }
    return result;
  }
}
