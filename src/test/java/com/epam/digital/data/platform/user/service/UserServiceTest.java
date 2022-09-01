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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epam.digital.data.platform.user.config.GenericConfig;
import com.epam.digital.data.platform.user.exception.MappingException;
import com.epam.digital.data.platform.user.model.User;
import com.epam.digital.data.platform.user.util.TestUtils;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {GenericConfig.class, UserService.class})
class UserServiceTest {

  @Autowired
  UserService userService;

  private byte[] content;

  @BeforeEach
  void beforeEach() {
    content = TestUtils.getContent("json/users.csv").getBytes(StandardCharsets.UTF_8);
  }

  @Test
  void happyPath() {
    var csv = new String(content, StandardCharsets.UTF_8);

    var users = userService.getCsvUsers(csv);

    assertThat(users).hasSize(5);
  }

  @Test
  void shouldDeleteUtf8BomAndParseSuccessfully() {
    byte[] contentWithBom = addUtf8Bom(content);
    var csv = new String(contentWithBom, StandardCharsets.UTF_8);

    var users = userService.getCsvUsers(csv);

    assertThat(users).hasSize(5);
  }

  @Test
  void shouldConvertIOExceptionToMappingExceptionWhenFailToParseCsv() {
    byte[] contentWith2Boms = addUtf8Bom(addUtf8Bom(content));
    var csv = new String(contentWith2Boms, StandardCharsets.UTF_8);

    assertThrows(MappingException.class, () -> userService.getCsvUsers(csv));
  }

  @Test
  void shouldSetUserNames() {
    var csv = new String(content, StandardCharsets.UTF_8);
    var csvUsers = userService.getCsvUsers(csv);
    var users = userService.convertToKeycloakUsers(csvUsers);

    for (User user : users) {
      assertThat(user.getUsername()).isNotNull();
    }
  }

  @Test
  void shouldConvertUsersToEnumerableUsersSequentially() {
    var csv = new String(content, StandardCharsets.UTF_8);
    var csvUsers = userService.getCsvUsers(csv);
    var users = userService.convertToKeycloakUsers(csvUsers);

    var enumerableUsers = userService.convertToEnumerableUsers(users);

    for (int i = 0; i < users.size(); i++) {
      assertThat(enumerableUsers.get(i).getSerialNumber()).isEqualTo(i);
      assertThat(enumerableUsers.get(i).getUsername()).isEqualTo(users.get(i).getUsername());
    }
  }

  private byte[] addUtf8Bom(byte[] content) {
    byte[] result = new byte[content.length + 3];
    result[0] = (byte) 0xef;
    result[1] = (byte) 0xbb;
    result[2] = (byte) 0xbf;
    System.arraycopy(content, 0, result, 3, content.length);
    return result;
  }
}