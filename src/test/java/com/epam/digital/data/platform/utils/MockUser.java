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

package com.epam.digital.data.platform.utils;

import static com.epam.digital.data.platform.user.model.CsvUser.DRFO;
import static com.epam.digital.data.platform.user.model.CsvUser.EDRPOU;
import static com.epam.digital.data.platform.user.model.CsvUser.FULL_NAME;
import static com.epam.digital.data.platform.user.model.CsvUser.KATOTTG;

import com.epam.digital.data.platform.user.model.User;
import java.util.List;

public class MockUser {

  public static UserBuilder user() {
    return new UserBuilder();
  }

  public static final class UserBuilder {

    private final User user = new User();

    public UserBuilder username(String username) {
      user.setUsername(username);
      return this;
    }

    public UserBuilder drfo(List<String> drfo) {
      user.getAttributes().put(DRFO, drfo);
      return this;
    }

    public UserBuilder edrpou(List<String> edrpou) {
      user.getAttributes().put(EDRPOU, edrpou);
      return this;
    }

    public UserBuilder fullName(List<String> fullName) {
      user.getAttributes().put(FULL_NAME, fullName);
      return this;
    }

    public UserBuilder realmRoles(List<String> realmRoles) {
      user.setRealmRoles(realmRoles);
      return this;
    }

    public UserBuilder katottg(List<String> katottg) {
      user.getAttributes().put(KATOTTG, katottg);
      return this;
    }

    public UserBuilder enabled(boolean enabled) {
      user.setEnabled(enabled);
      return this;
    }

    public User build() {
      return user;
    }
  }
}
