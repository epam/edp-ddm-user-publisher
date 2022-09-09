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

import static com.epam.digital.data.platform.utils.MockCsvUser.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.user.config.ValidationConfig;
import com.epam.digital.data.platform.user.model.CsvUser;
import com.epam.digital.data.platform.user.provider.ExistingRolesProvider;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ValidationConfig.class, ValidationService.class})
class ValidationServiceTest {

  @MockBean
  ExistingRolesProvider existingRolesProvider;
  @Autowired
  ValidationService validationService;

  @BeforeEach
  void beforeEach() {
    when(existingRolesProvider.getExistingRoles()).thenReturn(Set.of("officer"));
  }

  @Test
  void happyPathDoesNotWriteAnyErrors() {
    var result = validationService.validate(List.of(mockUser()));
    assertThat(result).isEmpty();
  }

  @Nested
  class CheckThatEachDrfoValidatorAddedToChain {

    @Test
    void drfoPresenceValidatorChecking() {
      CsvUser user = mockUser();
      user.setDrfo("");

      var result = validationService.validate(List.of(user));

      assertThat(result).hasSize(1);
      assertThat(result.get(0).get(0)).isEqualTo(
          "User does not contain DRFO");
    }
  }

  @Nested
  class CheckThatEachEdrpouValidatorAddedToChain {

    @Test
    void edrpouPresenceValidatorChecking() {
      CsvUser user = mockUser();
      user.setEdrpou("");

      var result = validationService.validate(List.of(user));

      assertThat(result).hasSize(1);
      assertThat(result.get(0).get(0)).isEqualTo(
          "User does not contain EDRPOU");
    }

    @Test
    void edrpouCharactersValidatorChecking() {
      CsvUser user = mockUser();
      user.setEdrpou("1111f");

      var result = validationService.validate(List.of(user));

      assertThat(result).hasSize(1);
      assertThat(result.get(0).get(0)).isEqualTo(
          "EDRPOU must contain only digits");
    }
  }

  @Nested
  class CheckThatEachFullNameValidatorAddedToChain {

    @Test
    void fullNamePresenceValidatorChecking() {
      CsvUser user = mockUser();
      user.setFullName("");

      var result = validationService.validate(List.of(user));

      assertThat(result).hasSize(1);
      assertThat(result.get(0).get(0)).isEqualTo(
          "User does not contain FullName");
    }
  }

  @Nested
  class CheckThatEachRoleValidatorAddedToChain {

    @Test
    void roleCountValidatorChecking() {
      CsvUser user = mockUser();
      user.setRealmRoles(List.of());

      var result = validationService.validate(List.of(user));

      assertThat(result).hasSize(1);
      assertThat(result.get(0).get(0)).isEqualTo(
          "User must contain at least one role, but contains zero");
    }

    @Test
    void rolePresenceValidatorChecking() {
      CsvUser user = mockUser();
      user.setRealmRoles(List.of(""));

      var result = validationService.validate(List.of(user));

      assertThat(result).hasSize(1);
      assertThat(result.get(0).get(0)).isEqualTo(
          "Role cannot be null or empty string");
    }


    @Test
    void rolePresenceInRealmValidatorChecking() {
      CsvUser user = mockUser();
      user.setRealmRoles(List.of("head-officer"));

      var result = validationService.validate(List.of(user));

      assertThat(result).hasSize(1);
      assertThat(result.get(0).get(0)).isEqualTo(
          "Realm does not contain role: head-officer");
    }
  }

  private CsvUser mockUser() {
    return
        user()
            .drfo("11112222")
            .edrpou("33334444")
            .fullName("some-full-name")
            .realmRoles(List.of("officer"))
            .build();
  }
}
