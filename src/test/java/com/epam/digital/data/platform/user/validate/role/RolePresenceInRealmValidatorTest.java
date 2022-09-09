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

package com.epam.digital.data.platform.user.validate.role;

import static com.epam.digital.data.platform.utils.MockCsvUser.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.user.model.ValidationResult;
import com.epam.digital.data.platform.user.provider.ExistingRolesProvider;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RolePresenceInRealmValidatorTest {

  private RolePresenceInRealmValidator validator;
  private ExistingRolesProvider existingRolesProvider;

  @BeforeEach
  void beforeEach() {
    existingRolesProvider = mock(ExistingRolesProvider.class);
    validator = new RolePresenceInRealmValidator(existingRolesProvider);
  }

  @Test
  void happyPathDoesNotWriteAnyErrors() {
    when(existingRolesProvider.getExistingRoles()).thenReturn(Set.of("officer"));

    var result = validator.validate(1, user().realmRoles(List.of("officer")).build(),
        new ValidationResult());

    assertThat(result).isEmpty();
  }

  @Test
  void writesErrorWhenUsersRoleAbsentInRealm() {
    when(existingRolesProvider.getExistingRoles()).thenReturn(Set.of("officer"));

    var result = validator.validate(1,
        user().realmRoles(List.of("officer", "head-officer")).build(), new ValidationResult());

    assertThat(result).hasSize(1);
    assertThat(result.get(1).get(0)).isEqualTo("Realm does not contain role: head-officer");
  }
}
