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

import static com.epam.digital.data.platform.utils.MockUser.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.user.model.SkippingResult;
import com.epam.digital.data.platform.user.provider.ExistingUsersProvider;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EqualsNameEqualsAttributesSkipperTest {

  private EqualsNameEqualsAttributesSkipper skipper;
  private ExistingUsersProvider existingUsersProvider;

  @BeforeEach
  void beforeEach() {
    existingUsersProvider = mock(ExistingUsersProvider.class);
    skipper = new EqualsNameEqualsAttributesSkipper(existingUsersProvider);
  }

  @Test
  void shouldSkipUserIfExistUserWithTheSameNameAndTheSameAttributes() {
    var existingUsers = List.of(user().username("some-user-name").build());
    when(existingUsersProvider.getExistingUsers()).thenReturn(existingUsers);
    var user1 = user().drfo(List.of("22222222")).build();
    var user2 = user().username("another-user-name").build();
    var usersForImport = List.of(user1, user().username("some-user-name").build(), user2);

    var result = skipper.check(usersForImport, new SkippingResult());

    assertEquals(1, result.size());
    assertThat(result.get(1).get(0)).isEqualTo(
        "The user with name 'some-user-name' will be skipped because there is already a user with "
            + "the same name and attributes (drfo, edrpou, fullName)");
  }
}
