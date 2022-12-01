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

package com.epam.digital.data.platform.user.service;

import static com.epam.digital.data.platform.user.model.CsvUser.DRFO;
import static com.epam.digital.data.platform.user.model.CsvUser.EDRPOU;
import static com.epam.digital.data.platform.user.model.CsvUser.FULL_NAME;
import static com.epam.digital.data.platform.user.model.CsvUser.KATOTTG;
import static com.epam.digital.data.platform.user.util.Constants.FIRST_USER_OFFSET;

import com.epam.digital.data.platform.user.model.CsvUser;
import com.epam.digital.data.platform.user.model.EnumerableUser;
import com.epam.digital.data.platform.user.model.User;
import com.epam.digital.data.platform.user.util.KatottgUtil;
import com.epam.digital.data.platform.user.util.UserNameUtils;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class UserService {

  private final CsvParser csvParser;

  public UserService(CsvParser csvParser) {
    this.csvParser = csvParser;
  }

  public List<CsvUser> getCsvUsers(String csv) {
    return csvParser.getCsvUsers(csv);
  }

  public List<User> convertToKeycloakUsers(List<CsvUser> csvUsers) {
    List<User> result = new ArrayList<>(csvUsers.size());
    for (var csvUser : csvUsers) {
      result.add(getKeycloakUser(csvUser));
    }
    return result;
  }

  public List<EnumerableUser> convertToEnumerableUsers(List<User> users) {
    List<EnumerableUser> result = new ArrayList<>(users.size());
    int i = 0;
    for (User user : users) {
      var tempUser = new EnumerableUser(user);
      tempUser.setSerialNumber(i++);
      result.add(tempUser);
    }
    return result;
  }

  public List<Integer> collapseKatottg(List<EnumerableUser> users) {
    List<Integer> result = new ArrayList<>();
    for (var user : users) {
      List<String> katottgs = user.getAttributes().get(KATOTTG);
      if (katottgs != null) {
        int beforeFiltering = katottgs.size();
        user.getAttributes().put(KATOTTG, KatottgUtil.retainPrefixes(katottgs));
        int afterFiltering = user.getAttributes().get(KATOTTG).size();
        if (beforeFiltering != afterFiltering) {
          result.add(user.getSerialNumber() + FIRST_USER_OFFSET);
        }
      }
    }
    return result;
  }

  private User getKeycloakUser(CsvUser csvUser) {
    var user = new User();
    putIfPresent(user.getAttributes(), DRFO, List.of(csvUser.getDrfo()));
    putIfPresent(user.getAttributes(), EDRPOU, List.of(csvUser.getEdrpou()));
    putIfPresent(user.getAttributes(), FULL_NAME, List.of(csvUser.getFullName()));
    putIfPresent(user.getAttributes(), KATOTTG, csvUser.getKatottg());

    user.getAttributes()
        .putAll(csvUser.getCustomAttributes() == null ? Map.of() : csvUser.getCustomAttributes());

    user.setRealmRoles(csvUser.getRealmRoles());
    user.setFirstName(UserNameUtils.getFirstAndMiddleName(csvUser.getFullName()));
    user.setLastName(UserNameUtils.getLastName(csvUser.getFullName()));
    user.setUsername(createUsername(csvUser));
    return user;
  }

  private void putIfPresent(Map<String, List<String>> map, String key, List<String> value) {
    if (value != null && !value.isEmpty()) {
      map.put(key, value);
    }
  }

  private String createUsername(CsvUser user) {
    var str = user.getFullName().toLowerCase() + user.getEdrpou() + user.getDrfo();
    return DigestUtils.sha256Hex(str.getBytes(StandardCharsets.UTF_8));
  }
}
