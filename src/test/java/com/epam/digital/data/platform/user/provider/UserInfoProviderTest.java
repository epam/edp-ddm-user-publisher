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

package com.epam.digital.data.platform.user.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epam.digital.data.platform.user.exception.JwtValidationException;
import com.epam.digital.data.platform.user.service.TokenParser;
import com.epam.digital.data.platform.user.util.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class UserInfoProviderTest {

  private UserInfoProvider userInfoProvider;

  @Test
  void shouldReturnCorrectUsersInfo() {
    var token = TestUtils.getContent("user-token");
    userInfoProvider = new UserInfoProvider(token, new TokenParser(new ObjectMapper()));

    var auditUserInfo = userInfoProvider.getUserInfo();

    assertThat(auditUserInfo.getUserDrfo()).isEqualTo("1010101014");
    assertThat(auditUserInfo.getUserName()).isEqualTo("Сидоренко Василь Леонідович");
    assertThat(auditUserInfo.getUserKeycloakId()).isEqualTo("496fd2fd-3497-4391-9ead-41410522d06f");
  }

  @Test
  void exceptionIfAnyRequiredFieldAbsent() {
    var token = TestUtils.getContent("service-account-token");
    userInfoProvider = new UserInfoProvider(token, new TokenParser(new ObjectMapper()));

    var ex = assertThrows(JwtValidationException.class, () -> userInfoProvider.getUserInfo());
    assertThat(ex.getMessage()).isEqualTo(
        "Users access token does not have all required parameters (drfo, fullName, subject)");
  }
}