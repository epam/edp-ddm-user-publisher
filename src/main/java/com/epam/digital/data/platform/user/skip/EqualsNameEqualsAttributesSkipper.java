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

import com.epam.digital.data.platform.user.model.SkippingResult;
import com.epam.digital.data.platform.user.model.User;
import com.epam.digital.data.platform.user.provider.ExistingUsersProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EqualsNameEqualsAttributesSkipper extends Skipper {

  private final ExistingUsersProvider existingUsersProvider;

  public EqualsNameEqualsAttributesSkipper(ExistingUsersProvider existingUsersProvider) {
    this.existingUsersProvider = existingUsersProvider;
  }

  @Override
  public SkippingResult check(List<User> users, SkippingResult results) {
    var existingUsers = existingUsersProvider.getExistingUsers();
    var usersWithEqNameEqAttributes = getUsersWithEqNameEqAttributes(existingUsers, users);
    for (var entry : usersWithEqNameEqAttributes.entrySet()) {
      results.add(entry.getKey(), createMessage(entry));
    }
    return checkNext(users, results);
  }

  private String createMessage(Entry<Integer, User> entry) {
    return String.format("The user with name '%s' will be skipped because there is already a user "
            + "with the same name and attributes (drfo, edrpou, fullName)",
        entry.getValue().getUsername());
  }

  public Map<Integer, User> getUsersWithEqNameEqAttributes(List<User> existingUsers,
      List<User> users) {
    Map<String, User> existingUserMap = existingUsers.stream()
        .collect(Collectors.toMap(User::getUsername, Function.identity()));
    Map<Integer, User> result = new HashMap<>();
    for (int i = 0; i < users.size(); i++) {
      var existingUser = existingUserMap.get(users.get(i).getUsername());
      if (existingUser != null && existingUser.equals(users.get(i))) {
        result.put(i, existingUser);
      }
    }
    return result;
  }
}
