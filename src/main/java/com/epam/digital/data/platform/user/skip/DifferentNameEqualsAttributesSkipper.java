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
import java.util.stream.Collectors;

public class DifferentNameEqualsAttributesSkipper extends Skipper {

  private final ExistingUsersProvider existingUsersProvider;

  public DifferentNameEqualsAttributesSkipper(ExistingUsersProvider existingUsersProvider) {
    this.existingUsersProvider = existingUsersProvider;
  }

  @Override
  public SkippingResult check(List<User> users, SkippingResult results) {
    var existingUsers = existingUsersProvider.getExistingUsers();
    var usersWithDiffNameEqAttributes = getUsersWithDiffNameEqAttributes(existingUsers, users);
    for (var entry : usersWithDiffNameEqAttributes.entrySet()) {
      results.add(entry.getKey(), createMessage(entry));
    }
    return checkNext(users, results);
  }

  private String createMessage(Entry<Integer, List<User>> entry) {
    return String.format("The user will be skipped because there is already user with the same "
            + "attributes (drfo, edrpou, fullName) but different name. "
            + "Name of existing user: %s",
        entry.getValue().stream().map(User::getUsername).collect(Collectors.toList()));
  }

  public Map<Integer, List<User>> getUsersWithDiffNameEqAttributes(List<User> existingUsers,
      List<User> users) {
    Map<Integer, List<User>> result = new HashMap<>();
    for (int i = 0; i < users.size(); i++) {
      var currentUser = users.get(i);
      var theSameAttributesUsers = existingUsers.stream()
          .filter(x -> x.equals(currentUser))
          .collect(Collectors.toList());
      theSameAttributesUsers = theSameAttributesUsers.stream()
          .filter(user -> !user.getUsername().equals(currentUser.getUsername()))
          .collect(Collectors.toList());
      if (!theSameAttributesUsers.isEmpty()) {
        result.put(i, theSameAttributesUsers);
      }
    }
    return result;
  }
}
