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

import com.epam.digital.data.platform.user.model.CsvUser;
import java.util.List;
import java.util.Map;

public class MockCsvUser {

  public static UserBuilder user() {
    return new UserBuilder();
  }

  public static final class UserBuilder {

    private final CsvUser user = new CsvUser();

    public UserBuilder drfo(String drfo) {
      user.setDrfo(drfo);
      return this;
    }

    public UserBuilder edrpou(String edrpou) {
      user.setEdrpou(edrpou);
      return this;
    }

    public UserBuilder fullName(String fullName) {
      user.setFullName(fullName);
      return this;
    }

    public UserBuilder realmRoles(List<String> realmRoles) {
      user.setRealmRoles(realmRoles);
      return this;
    }

    public UserBuilder katottg(List<String> katottg) {
      user.setKatottg(katottg);
      return this;
    }

    public UserBuilder attributes(Map<String, List<String>> attributes) {
      user.setCustomAttributes(attributes);
      return this;
    }

    public CsvUser build() {
      return user;
    }
  }
}
