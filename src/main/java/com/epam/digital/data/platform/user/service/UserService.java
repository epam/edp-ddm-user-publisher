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

import com.epam.digital.data.platform.user.exception.MappingException;
import com.epam.digital.data.platform.user.model.CsvUser;
import com.epam.digital.data.platform.user.model.EnumerableUser;
import com.epam.digital.data.platform.user.model.User;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class UserService {

  public static final String UTF8_BOM = "\uFEFF";

  private final ObjectReader objectReader;

  public UserService(ObjectReader objectReader) {
    this.objectReader = objectReader;
  }

  public List<CsvUser> getCsvUsers(String csv) {
    csv = removeUtf8BomIfExist(csv);
    try {
      MappingIterator<CsvUser> iterator = objectReader.readValues(csv);
      return iterator.readAll();
    } catch (IOException e) {
      throw new MappingException("Unable to map csv file to List<User>", e);
    }
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

  private String removeUtf8BomIfExist(String s) {
    if (s.startsWith(UTF8_BOM)) {
      s = s.substring(1);
    }
    return s;
  }

  private User getKeycloakUser(CsvUser csvUser) {
    var user = new User();
    user.setDrfo(List.of(csvUser.getDrfo()));
    user.setEdrpou(List.of(csvUser.getEdrpou()));
    user.setFullName(List.of(csvUser.getFullName()));
    user.setRealmRoles(csvUser.getRealmRoles());

    user.setLastName(csvUser.getFullName());
    user.setUsername(createUsername(csvUser));
    return user;
  }

  private String createUsername(CsvUser user) {
    var str = user.getFullName().toLowerCase() + user.getEdrpou() + user.getDrfo();
    return DigestUtils.sha256Hex(str.getBytes(StandardCharsets.UTF_8));
  }
}
